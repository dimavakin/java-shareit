package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private Comment comment;
    private Booking booking;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john.doe@example.com");
        item = new Item(1L, "Item Name", "Item Description", true, user, null);
        itemDto = new ItemDto(1L, "Item Name", "Item Description", true, 1L);
        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Item Name");
        itemCreateDto.setDescription("Item Description");
        itemCreateDto.setAvailable(true);
        comment = new Comment(1L, "Comment text", item, user, LocalDateTime.now());
        commentDto = new CommentDto();
        commentDto.setText("Comment text");
        booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, user,
                Status.APPROVED);
    }

    @Test
    void testFindAllFromUserWhenItemsExistThenReturnItemDtos() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findByItemOwnerId(1L)).thenReturn(List.of(booking));
        when(commentRepository.findCommentsByUserId(1L)).thenReturn(List.of(comment));
        List<ItemWithBookingsCommentsDto> result = itemService.getItems(1L);
        assertThat(result).hasSize(1);
        verify(itemRepository, times(1)).findByOwnerId(1L);
        verify(bookingRepository, times(1)).findByItemOwnerId(1L);
        verify(commentRepository, times(1)).findCommentsByUserId(1L);
    }

    @Test
    void testFindByIdWhenItemDoesNotExistThenThrowNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.findById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("id - 1 не найден");
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateWhenItemIsUpdatedThenReturnUpdatedItemDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(Mockito.any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setName(itemDto.getName());
            savedItem.setDescription(itemDto.getDescription());
            savedItem.setAvailable(itemDto.getAvailable());
            return savedItem;
        });
        ItemDto result = itemService.updateItem(1L, 1L, itemDto);
        assertThat(result.getName()).isEqualTo(itemDto.getName());
        assertThat(result.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(result.getAvailable()).isEqualTo(itemDto.getAvailable());
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(Mockito.any(Item.class));
    }

    @Test
    void testCreateWhenItemIsCreatedThenReturnItemDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);
        ItemDto result = itemService.addNewItem(1L, itemCreateDto);
        assertThat(result).isEqualTo(itemDto);
        verify(itemRepository, times(1)).save(Mockito.any(Item.class));
    }

    @Test
    void testCreateCommentWhenCommentIsCreatedThenReturnCommentDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        when(bookingRepository.findByBookerIdWithItem(1L)).thenReturn(List.of(booking));
        when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);
        CommentDto result = itemService.addNewComment(commentDto, 1L, 1L);
        assertThat(result.getText()).isEqualTo(commentDto.getText());
        verify(commentRepository, times(1)).save(Mockito.any(Comment.class));
    }

    @Test
    void testCreateWhenUserDoesNotExistThenThrowValidationException() {
        when(userRepository.findById(1L)).thenThrow(new ValidationException("Пользователь не найден"));
        assertThatThrownBy(() -> itemService.addNewItem(1L, itemCreateDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void testCreateCommentWhenUserIsNotBookerThenThrowValidationException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdWithItem(1L)).thenReturn(List.of());
        assertThatThrownBy(() -> itemService.addNewComment(commentDto, 1L, 1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь c id = 1 не был или не является арендатором вещи c id = 1");
    }
}