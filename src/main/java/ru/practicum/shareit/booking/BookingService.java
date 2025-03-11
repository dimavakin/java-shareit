package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingDto approveBooking(Long bookingId, boolean approved, Long userId);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getBookingForUser(Long userId, String state);

    List<BookingDto> getBookingsForOwner(Long ownerId, String state);
}
