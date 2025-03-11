package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking mapToBookingFromRequestDto(BookingRequestDto bookingRequestDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBooker(new UserDto(
                booking.getBooker().getId(),
                booking.getBooker().getName(),
                booking.getBooker().getEmail()));
        dto.setItem(new ItemDto(
                booking.getItem().getId(),
                booking.getItem().getName(),
                booking.getItem().getDescription(),
                booking.getItem().getAvailable(),
                booking.getItem().getOwner().getId()
        ));
        return dto;
    }

}
