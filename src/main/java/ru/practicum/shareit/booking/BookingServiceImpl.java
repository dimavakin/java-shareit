package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional()
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(Long userId, BookingRequestDto bookingRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("userId не найден: " + userId));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("itemId не найден: " + bookingRequestDto.getItemId()));
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет недоступен для бронирования: " + item.getId());
        }

        return BookingMapper.mapToBookingDto(
                bookingRepository.save(
                        BookingMapper.mapToBookingFromRequestDto(bookingRequestDto, user, item)));
    }

    @Override
    public BookingDto approveBooking(Long bookingId, boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new NotFoundException("bookingId не найден: " + bookingId));
        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else {
            throw new ValidationException(String.format("Пользователь с id = %d не является владельцем вещи с id = %d",
                    ownerId, booking.getItem().getOwner().getId()));
        }

        return BookingMapper.mapToBookingDto(bookingRepository
                .save(bookingRepository.save(booking)));
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        User bookerOrOwner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("userId не найден: " + userId));
        Booking booking = bookingRepository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        if (!booking.getItem().getOwner().equals(bookerOrOwner) && !booking.getBooker().equals(bookerOrOwner)) {
            throw new ValidationException("Пользователь не является владельцем вещи или автором бронирования");
        }
        return BookingMapper.mapToBookingDto(bookingRepository
                .save(bookingRepository.save(booking)));
    }

    @Override
    public List<BookingDto> getBookingForUser(Long userId, String state) {
        BookingState bookingState = BookingState.fromString(state);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;
        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsForOwner(Long ownerId, String state) {
        BookingState bookingState = BookingState.fromString(state);
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("userId не найден: " + ownerId));

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> bookings;
        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemIds, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemIds, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemIds, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Status.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByItemIdInOrderByStartDesc(itemIds);
        }

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }


}
