package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.mockito.ArgumentMatchers.anyInt;
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
                .id(1)
                .name("Apple MacBook Pro")
                .description("Новый MacBook Pro. Невероятная мощь с чипом M1 Pro или M1 Max.")
                .available(true)
                .build();

        user = User.builder()
                .id(1)
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
                .id(1)
                .text("Надёжный, эффективный, качественный.")
                .authorName("Тим Кук")
                .created(LocalDateTime.of(2022, 11, 17, 15, 21, 35))
                .build();
    }

    @Test
    void findAllByOwnerTest() throws Exception {
        when(itemService.findAllByOwner(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDtoWithBooking));

        mockMvc.perform(get("/items")
                        .header(USER_ID, "1")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoWithBooking.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithBooking.getName()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithBooking.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .findAllByOwner(anyInt(), anyInt(), anyInt());
    }

    @Test
    void findAllByOwnerInvalidFromParamTest() throws Exception {
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
        when(itemService.create(any(), anyInt()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .create(any(ItemDto.class), anyInt());
    }

    @Test
    void createWithBadRequestTest() throws Exception {
        when(itemService.create(any(), anyInt()))
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
    void findByIdTest() throws Exception {
        when(itemService.findById(anyInt(), anyInt()))
                .thenReturn(itemDtoWithBooking);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .findById(anyInt(), anyInt());
    }

    @Test
    void findByIdNotFoundTest() throws Exception {
        when(itemService.findById(anyInt(), anyInt()))
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
        when(itemService.update(any(), anyInt(), anyInt()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .update(any(), anyInt(), anyInt());
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
                .delete(anyInt());
    }

    @Test
    void searchTest() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
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
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .search(anyString(), anyInt(), anyInt());
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.createComment(anyInt(), anyInt(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));

        verify(itemService, times(1))
                .createComment(anyInt(), anyInt(), any());
    }
}