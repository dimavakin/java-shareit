package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<ItemWithBookingsCommentsDto> getItems(Long userId);

    ItemDto addNewItem(Long userId, ItemDto item);

    void deleteItem(Long userId, long itemId);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemWithCommentsDto findByID(Long itemId);

    List<ItemDto> findItemByText(String text);

    CommentDto addNewComment(CommentDto commentDto, Long itemId, Long userId);
}
