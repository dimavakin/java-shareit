package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping("/all")
    public ResponseEntity<Object> findAll() {
        return itemRequestClient.findAll();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable Long requestId) {
        return itemRequestClient.findById(requestId);
    }

    @GetMapping()
    public ResponseEntity<Object> findByRequestorId(@RequestHeader(value = "X-Sharer-User-Id", required = false)
                                                    Long requestorId) {
        return itemRequestClient.findByRequestorId(requestorId);
    }

    @PostMapping()
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestCreateDto,
                                                    @RequestHeader(value = "X-Sharer-User-Id", required = false) Long requestorId) {
        return itemRequestClient.createItemRequest(itemRequestCreateDto, requestorId);
    }
}