package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner().getId());
    }

    public static Item mapToItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), user);
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapToItemDto(item));
        }
        return dtos;
    }

    public static ItemWithCommentsDto mapToItemWithCommentsDto(Item item, List<Comment> comments) {
        ItemWithCommentsDto dto = new ItemWithCommentsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner().getId());
        dto.setComments(comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList()));
        return dto;
    }

    public static ItemWithBookingsCommentsDto mapToItemWithBookingsCommentsDto(
            Item item, List<Booking> bookings, List<Comment> comments) {
        ItemWithBookingsCommentsDto dto = new ItemWithBookingsCommentsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner().getId());
        dto.setBookings(bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList()));
        dto.setComments(comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList()));
        return dto;
    }
}
