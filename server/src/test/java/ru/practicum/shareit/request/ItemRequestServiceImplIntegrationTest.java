package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseSpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemRequestServiceImplIntegrationTest extends BaseSpringBootTest {

    private User requestor;
    private ItemRequestCreateDto itemRequestCreateDto;

    @BeforeEach
    public void setUp() {
        requestor = new User(null, "Requestor User", "requestor@example.com");
        userRepository.save(requestor);
        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("Need a tool");
    }

    @Test
    public void testCreateItemRequestWhenRequestIsValidThenReturnCreatedRequest() {
        ItemRequestDto createdRequest = itemRequestService.createItemRequest(itemRequestCreateDto, requestor.getId());
        assertThat(createdRequest).isNotNull();
        assertThat(createdRequest.getDescription()).isEqualTo(itemRequestCreateDto.getDescription());
    }

    @Test
    public void testFindByIdWhenRequestExistsThenReturnRequest() {
        ItemRequestDto createdRequest = itemRequestService.createItemRequest(itemRequestCreateDto, requestor.getId());
        ItemRequestDto foundRequest = itemRequestService.findById(createdRequest.getId());
        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getDescription()).isEqualTo(createdRequest.getDescription());
    }

    @Test
    public void testFindByIdWhenRequestDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findById(999L));
    }

    @Test
    public void testFindByRequestorIdWhenRequestsExistThenReturnListOfRequests() {
        itemRequestService.createItemRequest(itemRequestCreateDto, requestor.getId());
        List<ItemRequestDto> requests = itemRequestService.findByRequestorId(requestor.getId());
        assertThat(requests).hasSize(1);
        assertThat(requests.getFirst().getDescription()).isEqualTo(itemRequestCreateDto.getDescription());
    }

    @Test
    public void testFindAllWhenRequestsExistThenReturnListOfRequests() {
        itemRequestService.createItemRequest(itemRequestCreateDto, requestor.getId());
        List<ItemRequestDto> requests = itemRequestService.findAll();
        assertThat(requests).hasSize(1);
    }

    @Test
    public void testCreateItemRequestWhenUserDoesNotExistThenThrowNotFoundException() {
        Long nonExistentUserId = 999L;
        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(itemRequestCreateDto, nonExistentUserId));
    }

    @Test
    public void testFindByRequestorIdWhenUserDoesNotExistThenThrowNotFoundException() {
        Long nonExistentUserId = 999L;
        assertThrows(NotFoundException.class, () -> itemRequestService.findByRequestorId(nonExistentUserId));
    }

    @Test
    public void testFindByRequestorIdWhenNoRequestsExistThenReturnEmptyList() {
        User anotherUser = new User(null, "Another User", "another@example.com");
        userRepository.save(anotherUser);

        List<ItemRequestDto> requests = itemRequestService.findByRequestorId(anotherUser.getId());
        assertThat(requests).isEmpty();
    }

    @Test
    public void testFindAllWhenNoRequestsExistThenReturnEmptyList() {
        List<ItemRequestDto> requests = itemRequestService.findAll();
        assertThat(requests).isEmpty();
    }
}