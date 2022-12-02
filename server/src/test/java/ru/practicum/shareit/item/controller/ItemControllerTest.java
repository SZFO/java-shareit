package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    private static final String USER_ID = "X-Sharer-User-Id";

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    ItemDto itemDto;

    Item item;

    User user;

    ItemDtoWithBooking itemDtoWithBooking;

    CommentDto commentDto;

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Apple MacBook Pro")
                .description("Новый MacBook Pro. Невероятная мощь с чипом M1 Pro или M1 Max.")
                .available(true)
                .build();

        user = User.builder()
                .id(1L)
                .name("Вадим Фаустов")
                .email("vadimfaustov@gmail.com")
                .build();

        item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(user)
                .available(true)
                .build();

        itemDtoWithBooking = ItemDtoWithBooking.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Надёжный, эффективный, качественный.")
                .authorName("Тим Кук")
                .created(LocalDateTime.of(2022, 11, 17, 15, 21, 35))
                .build();
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        when(itemService.getAllByOwner(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(itemDtoWithBooking));

        mockMvc.perform(get("/items")
                        .header(USER_ID, "1")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithBooking.getName()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithBooking.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .getAllByOwner(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllByOwnerInvalidFromParamTest() throws Exception {
        mockMvc.perform(get("/items")
                        .header(USER_ID, "1")
                        .param("from", "-100")
                        .param("size", "5")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createTest() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .create(any(ItemDto.class), anyLong());
    }

    @Test
    void createWithBadRequestTest() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenThrow(BadRequestException.class);

        mockMvc.perform(post("/items")
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByIdTest() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemDtoWithBooking);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .getById(anyLong(), anyLong());
    }

    @Test
    void getByIdNotFoundTest() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTest() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .update(any(), anyLong(), anyLong());
    }

    @Test
    void deleteTest() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", itemDto.getId())
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(itemService, times(1))
                .delete(anyLong());
    }

    @Test
    void searchTest() throws Exception {
        when(itemService.search(anyString(), any(Pageable.class)))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(USER_ID, "1")
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "5")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .search(anyString(), any(Pageable.class));
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));

        verify(itemService, times(1))
                .createComment(anyLong(), anyLong(), any());
    }
}