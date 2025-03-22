package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private User booker;
    private User itemOwner;
    private Item item;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "John Doe", "john.doe@example.com");
        itemOwner = new User(2L, "Item Owner", "owner@example.com");
        item = new Item(1L, "Item Name", "Item Description", true, itemOwner, null);
        booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                item, booker, Status.APPROVED);
        bookingRequestDto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId(), booker.getId());
    }

    @Test
    void testCreateBookingWhenValidRequestThenReturnBookingDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto result = bookingService.addBooking(bookingRequestDto, booker.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getStart()).isEqualTo(booking.getStart());
        assertThat(result.getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void testCreateBookingWhenUserNotFoundThenThrowException() {
        when(userRepository.findById(booker.getId())).thenThrow(new NotFoundException("User not found"));
        assertThatThrownBy(() -> bookingService.addBooking(bookingRequestDto, booker.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void testCreateBookingWhenItemNotFoundThenThrowException() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenThrow(new NotFoundException("Item not found"));
        assertThatThrownBy(() -> bookingService.addBooking(bookingRequestDto, booker.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item not found");
    }

    @Test
    void testApproveBookingWhenOwnerApprovesThenReturnUpdatedBookingDto() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findByIdWithBookerAndItem(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking); // Настраиваем возврат сохраненного объекта
        BookingDto result = bookingService.approveBooking(booking.getId(), true, item.getOwner().getId());
        assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void testApproveBookingWhenNotOwnerThenThrowException() {
        when(bookingRepository.findByIdWithBookerAndItem(booking.getId())).thenReturn(Optional.of(booking));
        assertThatThrownBy(() -> bookingService.approveBooking(booking.getId(), true, 999L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("не является владельцем вещи");
    }

    @Test
    void testFindBookingWhenBookingExistsThenReturnBookingDto() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByIdWithBookerAndItem(booking.getId())).thenReturn(Optional.of(booking));
        BookingDto result = bookingService.getBooking(booking.getId(), booker.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    void testFindBookingWhenBookingDoesNotExistThenThrowException() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByIdWithBookerAndItem(booking.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookingService.getBooking(booking.getId(), booker.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Бронирование с id=");
    }

    @Test
    void testFindBookingWhenUserIsNotBookerOrOwnerThenThrowException() {
        User anotherUser = new User(2L, "Jane Doe", "jane.doe@example.com");
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.of(anotherUser));
        when(bookingRepository.findByIdWithBookerAndItem(booking.getId())).thenReturn(Optional.of(booking));
        assertThatThrownBy(() -> bookingService.getBooking(booking.getId(), anotherUser.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь не является владельцем вещи или автором бронирования");
    }

    @Test
    void testFindBookerBookingsForCurrentState() {
        LocalDateTime now = LocalDateTime.now();
        Booking currentBooking = new Booking(2L, now.minusHours(1), now.plusHours(1), item, booker, Status.APPROVED);
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(currentBooking));
        List<BookingDto> bookings = bookingService.getBookingForUser(BookingState.CURRENT.name(), booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void testFindBookerBookingsForPastState() {
        LocalDateTime past = LocalDateTime.now().minusDays(2);
        Booking pastBooking = new Booking(3L, past.minusDays(1), past, item, booker, Status.APPROVED);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(pastBooking));
        List<BookingDto> bookings = bookingService.getBookingForUser(BookingState.PAST.name(), booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getEnd()).isBefore(LocalDateTime.now());
    }

    @Test
    void testFindBookerBookingsForFutureState() {
        LocalDateTime future = LocalDateTime.now().plusDays(2);
        Booking futureBooking = new Booking(4L, future, future.plusDays(1), item, booker, Status.WAITING);
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(futureBooking));
        List<BookingDto> bookings = bookingService.getBookingForUser(BookingState.FUTURE.name(), booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStart()).isAfter(LocalDateTime.now());
    }

    @Test
    void testFindBookerBookingsForWaitingState() {
        Booking waitingBooking = new Booking(5L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, Status.WAITING);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), eq(Status.WAITING)))
                .thenReturn(List.of(waitingBooking));
        List<BookingDto> bookings = bookingService.getBookingForUser(BookingState.WAITING.name(), booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void testFindBookerBookingsForRejectedState() {
        Booking rejectedBooking = new Booking(6L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, Status.REJECTED);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), eq(Status.REJECTED)))
                .thenReturn(List.of(rejectedBooking));
        List<BookingDto> bookings = bookingService.getBookingForUser(BookingState.REJECTED.name(), booker.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void testFindOwnerBookingsForAllState() {
        when(userRepository.findById(itemOwner.getId())).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findByItemOwnerId(anyLong())).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getBookingsForOwner(BookingState.ALL.name(), itemOwner.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
        verify(bookingRepository).findByItemOwnerId(eq(itemOwner.getId()));
    }

    @Test
    void testFindOwnerBookingsWithNoBookingsThenReturnEmptyList() {
        when(userRepository.findById(itemOwner.getId())).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findByItemOwnerId(anyLong())).thenReturn(Collections.emptyList());
        List<BookingDto> bookings = bookingService.getBookingsForOwner(BookingState.ALL.name(), itemOwner.getId());
        assertThat(bookings).isEmpty();
    }
}