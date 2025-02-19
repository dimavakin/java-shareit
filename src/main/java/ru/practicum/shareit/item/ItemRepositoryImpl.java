package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private List<Item> items = new ArrayList<>();
    private Long id = 0L;

    public Item save(Item item, Long userId) {
        item.setId(id);
        item.setUserId(userId);
        items.add(item);
        id += 1;
        return item;
    }

    @Override
    public List<Item> findByUserId(long userId) {
        return items.stream()
                .filter(item -> Objects.equals(item.getUserId(), userId))
                .toList();
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        items = items.stream()
                .filter(item -> !Objects.equals(item.getId(), itemId)
                        && !Objects.equals(item.getUserId(), userId))
                .toList();
    }

    @Override
    public Item patchItem(Long itemId, Item newItem, Long userId) {
        Optional<Item> itemOptional = items.stream()
                .filter(item -> item.getId().equals(itemId) && item.getUserId().equals(userId))
                .findFirst();
        if (itemOptional.isEmpty()) {
            throw new NotFoundException("Не нашелся айтем с таким ID");
        }
        if (newItem.getName() != null) {
            itemOptional.ifPresent(item -> item.setName(newItem.getName()));
        }
        if (newItem.getDescription() != null) {
            itemOptional.ifPresent(item -> item.setDescription(newItem.getDescription()));
        }
        if (newItem.getAvailable() != null) {
            itemOptional.ifPresent(item -> item.setAvailable(newItem.getAvailable()));
        }

        return itemOptional.get();
    }

    @Override
    public Item findItemById(Long itemId) {
        Optional<Item> itemOptional = items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
        if (itemOptional.isEmpty()) {
            throw new RuntimeException("Не нашелся айтем с таким ID");
        }

        return itemOptional.get();
    }

    @Override
    public List<Item> findItemByText(String text) {
        return items.stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable())
                .toList();
    }
}
