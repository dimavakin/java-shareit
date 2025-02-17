package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long userId);

    ItemDto addNewItem(Long userId, ItemDto item);

    void deleteItem(Long userId, long itemId);

    ItemDto patchItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto findItemByID(Long itemId);

    List<ItemDto> findItemByText(String text);
}
