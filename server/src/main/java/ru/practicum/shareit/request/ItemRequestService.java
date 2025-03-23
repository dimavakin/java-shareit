package ru.practicum.shareit.request;


import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, Long requestorId);

    ItemRequestDto findById(Long requestId);

    List<ItemRequestDto> findByRequestorId(Long requestorId);

    List<ItemRequestDto> findAll();
}
