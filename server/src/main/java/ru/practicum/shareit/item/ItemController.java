package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;

    @GetMapping
    public List<ItemWithBookingsCommentsDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody @Valid ItemCreateDto item) {
        log.warn(String.valueOf(userId));
        return itemService.addNewItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable(name = "itemId") Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto pathItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable(name = "itemId") Long itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentsDto findItemById(@PathVariable(name = "itemId") Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByText(@RequestParam String text) {
        if (text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        return itemService.findItemByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto setComment(@RequestBody CommentDto commentDto,
                                 @PathVariable(name = "itemId") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addNewComment(commentDto, itemId, userId);
    }
}