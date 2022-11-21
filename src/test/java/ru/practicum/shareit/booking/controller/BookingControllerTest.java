package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    private static final String USER_ID = "X-Sharer-User-Id";

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    User booker;

    User owner;

    Item item;

    BookingDto bookingDto;

    BookingRequestDto bookingRequestDto;

    @BeforeEach
    void init() {
        owner = User.builder()
                .id(1)
                .name("Вадим Фаустов")
                .email("vadimfaustov@gmail.com")
                .build();

        booker = User.builder()
                .id(2)
                .name("Тим Кук")
                .email("tcook@apple.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("Apple MacBook Pro")
                .description("Новый MacBook Pro. Невероятная мощь с чипом M1 Pro или M1 Max.")
                .owner(owner)
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .id(1)
                .item(item)
                .start(LocalDateTime.now().plusDays(7))
                .end(LocalDateTime.now().plusDays(30))
                .booker(booker)
                .status(Status.WAITING)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .itemId(bookingDto.getItem().getId())
                .build();
    }

    @Test
    void createTest() throws Exception {
        when(bookingService.save(anyInt(), any(BookingRequestDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, "2")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingService, times(1))
                .save(anyInt(), any(BookingRequestDto.class));
    }

    @Test
    void approveTest() throws Exception {
        when(bookingService.approve(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header(USER_ID, "2")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingService, times(1))
                .approve(anyInt(), anyInt(), anyBoolean());
    }


    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getById(anyInt(), anyInt()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .header(USER_ID, "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingService, times(1))
                .getById(anyInt(), anyInt());
    }

    @Test
    void getByBookerIdTest() throws Exception {
        when(bookingService.getAllByBookerId(anyInt(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID, booker.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        verify(bookingService, times(1))
                .getAllByBookerId(anyInt(), anyString(), any(Pageable.class));
    }

    @Test
    void getByOwnerIdTest() throws Exception {
        when(bookingService.getAllByOwnerId(anyInt(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID, owner.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        verify(bookingService, times(1))
                .getAllByOwnerId(anyInt(), anyString(), any(Pageable.class));
    }
}