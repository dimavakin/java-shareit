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
    public BookingDto addBooking(BookingRequestDto bookingRequestDto, Long userId) {
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

        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Бронирование уже подтверждено или отклонено");
        }

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
                .save(booking));
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
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingForUser(String state, Long userId) {
        BookingState bookingState = BookingState.fromString(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        if (state.equals(BookingState.ALL.name())) {
            bookings = bookingRepository.findByBookerIdWithDetails(userId);
        } else {
            bookings = switch (bookingState) {
                case CURRENT ->
                        bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            };
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsForOwner(String state, Long ownerId) {
        BookingState bookingState = BookingState.fromString(state);
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("userId не найден: " + ownerId));
        List<Booking> bookings;
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        if (state.equals(BookingState.ALL.name())) {
            bookings = bookingRepository.findByItemOwnerId(ownerId);
        } else {
            bookings = switch (bookingState) {
                case CURRENT ->
                        bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemIds, now, now);
                case PAST -> bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemIds, now);
                case FUTURE -> bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemIds, now);
                case WAITING -> bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Status.WAITING);
                case REJECTED -> bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Status.REJECTED);
                default -> bookingRepository.findByItemIdInOrderByStartDesc(itemIds);
            };
        }

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }
}
