package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setUserId(item.getOwner().getId());
        return dto;
    }

    public static Item mapToItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
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

    public static ItemResponseDto mapToItemResponseDto(Item item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }

    public static Item mapToItemFromCreateDto(ItemCreateDto itemDto, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setRequest(request);
        return item;
    }
}
