package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithCommentsDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
}
