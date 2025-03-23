package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseSpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemServiceImplIntegrationTest extends BaseSpringBootTest {

    private Item item;
    private ItemCreateDto itemCreateDto;
    private CommentDto commentDto;
    private User user;
    private ItemDto itemDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userRepository.save(user);
        item = new Item();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);
        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Item2");
        itemCreateDto.setDescription("Description2");
        itemCreateDto.setAvailable(true);
        itemDto = new ItemDto();
        itemDto.setName("Item1 Updated");
        itemDto.setDescription("Description1 Updated");
        itemDto.setAvailable(false);
        commentDto = new CommentDto();
        commentDto.setText("Great item!");
    }

    @Test
    public void testFindAllFromUserWhenItemsExistThenReturnListOfItems() {
        List<ItemWithBookingsCommentsDto> items = itemService.getItems(user.getId());
        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getName()).isEqualTo(item.getName());
        assertThat(items.getFirst().getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    public void testFindByIdWhenItemExistsThenReturnItem() {
        ItemWithCommentsDto foundItem = itemService.findById(item.getId());
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getName()).isEqualTo(item.getName());
        assertThat(foundItem.getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    public void testFindByIdWhenItemDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.findById(999L));
    }

    @Test
    public void testFindByTextWhenItemsExistThenReturnListOfItems() {
        List<ItemDto> items = itemService.findItemByText("Item1");
        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getName()).isEqualTo(item.getName());
        assertThat(items.getFirst().getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    public void testCreateWhenItemIsValidThenReturnCreatedItem() {
        ItemDto createdItem = itemService.addNewItem(user.getId(), itemCreateDto);
        assertThat(createdItem).isNotNull();
        assertThat(createdItem.getName()).isEqualTo(itemCreateDto.getName());
        assertThat(createdItem.getDescription()).isEqualTo(itemCreateDto.getDescription());
    }

    @Test
    public void testUpdateWhenItemIsValidThenReturnUpdatedItem() {
        ItemDto updatedItem = itemService.updateItem(user.getId(), item.getId(), itemDto);
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo(itemDto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(itemDto.getDescription());
    }

    @Test
    public void testUpdateWhenItemDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.updateItem(999L, user.getId(), itemDto));
    }

    @Test
    public void testCreateCommentWhenCommentIsValidThenReturnCreatedComment() {
        CommentDto createdComment = itemService.addNewComment(commentDto, item.getId(), user.getId());
        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getText()).isEqualTo(commentDto.getText());
    }

    @Test
    public void testFindAllFromUserWhenNoItemsExistThenReturnEmptyList() {
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("anotheruser@example.com");
        userRepository.save(anotherUser);

        List<ItemWithBookingsCommentsDto> items = itemService.getItems(anotherUser.getId());
        assertThat(items).isEmpty();
    }

    @Test
    public void testCreateWhenUserDoesNotExistThenThrowNotFoundException() {
        Long nonExistentUserId = 999L;
        assertThrows(NotFoundException.class, () -> itemService.addNewItem(nonExistentUserId, itemCreateDto));
    }

    @Test
    public void testDeleteWhenUserIsNotOwnerThenThrowNotFoundException() {
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("anotheruser@example.com");
        userRepository.save(anotherUser);

        assertThrows(NotFoundException.class, () -> itemService.deleteItem(anotherUser.getId(), item.getId()));
    }
}