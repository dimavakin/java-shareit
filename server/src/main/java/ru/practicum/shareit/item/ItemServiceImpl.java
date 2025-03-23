package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemWithBookingsCommentsDto> getItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("userId не найден " + userId));
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        List<Booking> bookings = bookingRepository.findByItemOwnerId(userId);
        List<Comment> comments = commentRepository.findCommentsByUserId(userId);
        return userItems.stream()
                .map(item -> {
                    List<Booking> bookingDtos = bookings.stream()
                            .filter(booking -> booking.getItem().getId().equals(item.getId()))
                            .toList();

                    List<Comment> commentDtos = comments.stream()
                            .filter(comment -> comment.getItem().getId().equals(item.getId()))
                            .toList();

                    return ItemMapper.mapToItemWithBookingsCommentsDto(item, bookingDtos, commentDtos);
                })
                .toList();
    }

    @Transactional
    @Override
    public ItemDto addNewItem(Long userId, ItemCreateDto itemCreateDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = null;
        if (itemCreateDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemCreateDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        }
        Item item = ItemMapper.mapToItemFromCreateDto(itemCreateDto, owner, request);
        Item savedItem = itemRepository.save(item);

        if (request != null) {
            request.getItems().add(item);
            itemRequestRepository.save(request);
        }

        return ItemMapper.mapToItemDto(savedItem);
    }

    @Transactional
    @Override
    public void deleteItem(Long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не является владельцем вещи с id=" + itemId);
        }
        itemRepository.deleteByOwnerIdAndId(userId, itemId);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemDto oldItem = ItemMapper.mapToItemDto(itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id=%d не найдена", itemId))));
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.mapToItemDto(itemRepository.save(ItemMapper.mapToItem(oldItem, owner)));
    }

    @Override
    public ItemWithCommentsDto findById(Long itemId) {
        Item findItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("id - " + itemId + " не найден"));
        List<Comment> comments = commentRepository.findCommentsByItemId(itemId);
        return ItemMapper.mapToItemWithCommentsDto(findItem, comments);
    }

    @Override
    public List<ItemDto> findItemByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text)
                .stream()
                .filter(Item::getAvailable)
                .toList();

        return ItemMapper.mapToItemDto(items);
    }

    @Override
    @Transactional
    public CommentDto addNewComment(CommentDto commentDto, Long itemId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("userId не найден" + userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("itemId не найден" + itemId));
        bookingRepository.findByBookerIdWithItem(userId).stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStatus().equals(Status.APPROVED)
                        && booking.getEnd().isBefore(LocalDateTime.now()))
                .findAny()
                .orElseThrow(() -> new ValidationException(
                        String.format("Пользователь c id = %d не был или не является арендатором вещи c id = %d",
                                userId, itemId)));
        Comment comment = CommentMapper.mapToCommentFromCreate(commentDto, user, item);

        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }
}
