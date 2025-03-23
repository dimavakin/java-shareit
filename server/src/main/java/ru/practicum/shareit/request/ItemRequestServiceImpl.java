package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, Long requestorId) {
        User user = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("userId не найден " + requestorId));

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository
                .save(ItemRequestMapper.toItemRequestFromCreateDto(itemRequestCreateDto, user)));
    }

    @Override
    public ItemRequestDto findById(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .map(ItemRequestMapper::toItemRequestDto)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос вещи с id=%d не найден", requestId)));
    }

    @Override
    public List<ItemRequestDto> findByRequestorId(Long requestorId) {
        if (!userRepository.existsById(requestorId)) {
            throw new NotFoundException("userId не найден " + requestorId);
        }

        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> findAll() {
        return itemRequestRepository.findAll()
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
    }
}
