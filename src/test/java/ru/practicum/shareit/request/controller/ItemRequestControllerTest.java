package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    private static final String USER_ID = "X-Sharer-User-Id";

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    ItemRequestDtoOut itemRequestDtoOut;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        itemRequestDtoOut = ItemRequestDtoOut.builder()
                .id(1)
                .description("Нужны наушники Apple AirPods Pro 2")
                .created(LocalDateTime.now())
                .items(null)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequestDtoOut.getId())
                .description(itemRequestDtoOut.getDescription())
                .created(itemRequestDtoOut.getCreated())
                .build();
    }

    @Test
    void createTest() throws Exception {
        when(itemRequestService.save(anyInt(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID, "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .save(anyInt(), any());
    }

    @Test
    void findByIdTest() throws Exception {
        when(itemRequestService.findById(anyInt(), anyInt()))
                .thenReturn(itemRequestDtoOut);

        mockMvc.perform(get("/requests/{requestId}", itemRequestDto.getId())
                        .header(USER_ID, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .findById(anyInt(), anyInt());
    }

    @Test
    void findAllTest() throws Exception {
        when(itemRequestService.findAll(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDtoOut));

        mockMvc.perform(get("/requests")
                        .header(USER_ID, "1")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .findAll(anyInt(), anyInt(), anyInt());
    }

    @Test
    void findAllFromOtherUserTest() throws Exception {
        when(itemRequestService.findAllFromOtherUser(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDtoOut));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID, "1")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .findAllFromOtherUser(anyInt(), anyInt(), anyInt());
    }
}