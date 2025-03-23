package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingRequestDto bookingRequestDto, Long userId);

    BookingDto approveBooking(Long bookingId, boolean approved, Long userId);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getBookingForUser(String state, Long userId);

    List<BookingDto> getBookingsForOwner(String state, Long ownerId);
}
