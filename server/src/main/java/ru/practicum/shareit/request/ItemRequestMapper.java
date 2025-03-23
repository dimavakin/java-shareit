package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemResponseDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("itemRequest не может быть null");
        }
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        dto.setRequestorId(itemRequest.getRequestor().getId());
        if (itemRequest.getItems() != null) {
            List<ItemResponseDto> itemResponseDtos = itemRequest.getItems().stream()
                    .map(ItemMapper::mapToItemResponseDto)
                    .toList();
            dto.setItems(itemResponseDtos);
        }
        return dto;
    }

    public static ItemRequest toItemRequestFromCreateDto(ItemRequestCreateDto dto, User requestor) {
        if (dto == null || requestor == null) {
            throw new IllegalArgumentException("dto и requestor не может быть null");
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(dto.getCreated());
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }
}
