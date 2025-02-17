package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> findByUserId(long userId);

    Item save(Item item, Long userId);

    void deleteByUserIdAndItemId(long userId, long itemId);

    Item patchItem(Long itemId, Item newItem, Long userId);

    Item findItemById(Long itemId);

    List<Item> findItemByText(String text);
}