package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping()
    public ResponseEntity<Object> findAllFromUser(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemClient.findAllFromUser(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItem(@PathVariable Long id,
                                           @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemClient.findById(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemByText(@RequestParam(required = false) String text,
                                                 @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemClient.findByText(text, userId);
    }

    @PostMapping()
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto,
                                         @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                         @PathVariable Long id) {
        return itemClient.update(itemDto, userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto comment,
                                                @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                @PathVariable Long itemId) {
        return itemClient.createComment(comment, userId, itemId);
    }
}