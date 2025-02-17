package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(Long userId) {
        return ItemMapper.mapToItemDto(itemRepository.findByUserId(userId));
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        if (userRepository.checkUserExists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        Item item = ItemMapper.mapToItem(itemDto, userId);
        Item savedItem = itemRepository.save(item, userId);

        return ItemMapper.mapToItemDto(savedItem);
    }

    @Override
    public void deleteItem(Long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDto patchItem(Long userId, Long itemId, ItemDto itemDto) {
        if (userRepository.checkUserExists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        Item item = ItemMapper.mapToItem(itemDto, userId);
        Item patchItem = itemRepository.patchItem(itemId, item, userId);

        return ItemMapper.mapToItemDto(patchItem);
    }

    @Override
    public ItemDto findItemByID(Long itemId) {
        Item findItem = itemRepository.findItemById(itemId);

        return ItemMapper.mapToItemDto(findItem);
    }

    @Override
    public List<ItemDto> findItemByText(String text) {
        List<Item> items = itemRepository.findItemByText(text);

        return ItemMapper.mapToItemDto(items);
    }
}
