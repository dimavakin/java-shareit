package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.base.BaseWebMvcTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


class ItemControllerTest extends BaseWebMvcTest {
    private ItemWithBookingsCommentsDto itemWithBookingsCommentsDto;
    private ItemWithCommentsDto itemWithCommentsDto;
    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private CommentDto commentDto;

    @BeforeEach
    public void setUp() {
        itemWithBookingsCommentsDto = new ItemWithBookingsCommentsDto(1L, "Item 1", "Description 1", true, 2L, null, null);
        itemWithCommentsDto = new ItemWithCommentsDto(1L, "Item 1", "Description 1", true, 2L, null, null, List.of());
        itemDto = new ItemDto(1L, "Item 1", "Description 1", true, 2L);
        itemCreateDto = new ItemCreateDto("Item 1", "Description 1", true, null);
        commentDto = new CommentDto(1L, "Comment 1", 1L, 1L, "Author 1", LocalDateTime.now());
    }

    @Test
    public void testFindAllFromUserWhenServiceReturnsItemsThenReturnListOfItems() throws Exception {
        List<ItemWithBookingsCommentsDto> items = Arrays.asList(itemWithBookingsCommentsDto);
        BDDMockito.given(itemService.getItems(1L)).willReturn(items);
        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemWithBookingsCommentsDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemWithBookingsCommentsDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemWithBookingsCommentsDto.getDescription()));
    }

    @Test
    public void testFindItemWhenServiceReturnsItemThenReturnItem() throws Exception {
        BDDMockito.given(itemService.findById(1L)).willReturn(itemWithCommentsDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemWithCommentsDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemWithCommentsDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemWithCommentsDto.getDescription()));
    }

    @Test
    public void testFindItemByTextWhenServiceReturnsItemsThenReturnListOfItems() throws Exception {
        List<ItemDto> items = Arrays.asList(itemDto);
        BDDMockito.given(itemService.findItemByText("Item")).willReturn(items);
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "Item"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()));
    }

    @Test
    public void testCreateWhenServiceCreatesItemThenReturnItem() throws Exception {
        BDDMockito.given(itemService.addNewItem(Mockito.eq(1L), Mockito.any(ItemCreateDto.class))).willReturn(itemDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item 1\",\"description\":\"Description 1\",\"available\":true}")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    public void testUpdateWhenServiceUpdatesItemThenReturnItem() throws Exception {
        BDDMockito.given(itemService.updateItem(Mockito.eq(1L), Mockito.eq(1L), Mockito.any(ItemDto.class))).willReturn(itemDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item 1 Updated\",\"description\":\"Description 1 Updated\",\"available\":true}")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    public void testCreateCommentWhenServiceCreatesCommentThenReturnComment() throws Exception {
        BDDMockito.given(itemService.addNewComment(Mockito.any(CommentDto.class), Mockito.eq(1L), Mockito.eq(1L))).willReturn(commentDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Comment 1\"}")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(commentDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value(commentDto.getText()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }
}