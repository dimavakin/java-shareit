package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(@RequestBody ItemRequestCreateDto itemRequestCreateDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.createItemRequest(itemRequestCreateDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getOne(@PathVariable Long requestId) {
        return itemRequestService.findById(requestId);
    }
}
