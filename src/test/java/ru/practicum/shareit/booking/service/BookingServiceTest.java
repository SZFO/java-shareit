package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.IncorrectStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.mapper.UserMapper.userToDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@WebMvcTest(BookingService.class)
@AutoConfigureMockMvc
class BookingServiceTest {
    BookingService bookingService;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    UserService userService;

    @MockBean
    ItemRepository itemRepository;

    Item item;

    User booker;

    User owner;

    Booking lastBooking;

    Booking nextBooking;

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

        lastBooking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now().minusDays(15))
                .end(LocalDateTime.now().minusDays(5))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        nextBooking = Booking.builder()
                .id(2)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .id(nextBooking.getId())
                .start(nextBooking.getStart())
                .end(nextBooking.getEnd())
                .build();

        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userService);
    }

    @Test
    void saveTest() {
        when(userService.getById(anyInt()))
                .thenReturn(userToDto(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any()))
                .thenReturn(nextBooking);

        BookingDto result = bookingService.save(booker.getId(), bookingRequestDto);

        assertNotNull(result);
        assertEquals(nextBooking.getId(), result.getId());
        assertEquals(nextBooking.getItem().getId(), result.getItem().getId());
    }

    @Test
    void saveOwnerAsBookerTest() {
        item.setOwner(booker);
        when(userService.getById(anyInt()))
                .thenReturn(userToDto(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any()))
                .thenReturn(nextBooking);

        Exception ex = assertThrows(NotFoundException.class, () -> bookingService.save(booker.getId(),
                bookingRequestDto));
        assertEquals("Владелец вещи не может забронировать свою вещь", ex.getMessage());
    }

    @Test
    void saveWrongEndTimeBooking() {
        bookingRequestDto.setEnd(LocalDateTime.now().minusDays(30));
        when(userService.getById(anyInt()))
                .thenReturn(userToDto(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        Exception ex = assertThrows(BadRequestException.class, () -> bookingService.save(booker.getId(),
                bookingRequestDto));
        assertEquals("Некорректное время окончания бронирования.", ex.getMessage());
    }

    @Test
    void saveWrongStartTimeBooking() {
        bookingRequestDto.setStart(LocalDateTime.now().minusHours(12));
        when(userService.getById(anyInt()))
                .thenReturn(userToDto(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        Exception ex = assertThrows(BadRequestException.class, () -> bookingService.save(booker.getId(),
                bookingRequestDto));
        assertEquals("Некорректное время начала бронирования.", ex.getMessage());
    }

    @Test
    void saveFalseAvailableTest() {
        item.setAvailable(false);
        when(userService.getById(anyInt()))
                .thenReturn(userToDto(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any()))
                .thenReturn(nextBooking);

        Exception ex = assertThrows(BadRequestException.class, () -> bookingService.save(booker.getId(),
                bookingRequestDto));
        assertEquals(String.format("Вещь с id = %s недоступна для бронирования.", item.getId()), ex.getMessage());
    }

    @Test
    void getByIdTest() {
        when(userService.getById(anyInt()))
                .thenReturn(userToDto(booker));
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(nextBooking));

        BookingDto result = bookingService.getById(nextBooking.getId(), booker.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(nextBooking.getId()));
        assertThat(result.getStatus(), equalTo(nextBooking.getStatus()));
        assertThat(result.getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getByIdNotFoundTest() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> bookingService.getById(nextBooking.getId(),
                owner.getId()));
        assertEquals(String.format(String.format("Бронирование с id = %s не существует.", nextBooking.getId())),
                ex.getMessage());
    }

    @Test
    void approveTest() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(nextBooking));
        when(bookingRepository.save(any()))
                .thenReturn(nextBooking);

        BookingDto result = bookingService.approve(nextBooking.getId(), owner.getId(), true);

        assertThat(result.getId(), equalTo(nextBooking.getId()));
        assertThat(result.getItem().getId(), equalTo(nextBooking.getItem().getId()));
        assertThat(result.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void approveNotFoundBookingTest() {
        when(userService.getById(anyInt()))
                .thenReturn(userToDto(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> bookingService.approve(nextBooking.getId(),
                owner.getId(), true));
        assertEquals(String.format("Бронирования с id = %s не существует.", nextBooking.getId()), ex.getMessage());
    }

    @Test
    void approveNoOwnerBookingTest() {
        when(userService.getById(anyInt()))
                .thenReturn(userToDto(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(lastBooking));

        Exception ex = assertThrows(NotFoundException.class, () -> bookingService.approve(lastBooking.getId(),
                booker.getId(), true));
        assertEquals("Подтвердить бронирование может только владелец вещи.", ex.getMessage());
    }

    @Test
    void approveAlreadyConfirmedTest() {
        when(userService.getById(anyInt()))
                .thenReturn(userToDto(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(lastBooking));

        Exception ex = assertThrows(BadRequestException.class, () -> bookingService.approve(lastBooking.getId(),
                owner.getId(), true));
        assertEquals("Бронирование уже было подтверждено.", ex.getMessage());
    }


    @Test
    void getAllByBookerIdStatusAllTest() {
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(nextBooking, lastBooking)));

        List<BookingDto> result = bookingService.getAllByBookerId(booker.getId(), "ALL", Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.get(0).getId(), equalTo(nextBooking.getId()));
        assertThat(result.get(1).getItem().getName(), equalTo(lastBooking.getItem().getName()));
    }

    @Test
    void getAllByBookerIdStatusCurrentTest() {
        Booking currentBooking = Booking.builder()
                .id(3)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findAllByStartBeforeAndEndAfterAndBookerIdOrderByStartDesc(
                any(), any(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(currentBooking)));

        List<BookingDto> result = bookingService.getAllByBookerId(booker.getId(), "CURRENT", Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.get(0).getId(), equalTo(currentBooking.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(currentBooking.getItem().getName()));
    }


    @Test
    void getAllByBookerIdStatusPastTest() {
        when(bookingRepository.findAllByEndBeforeAndBookerIdOrderByStartDesc(
                any(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(lastBooking)));

        List<BookingDto> result = bookingService.getAllByBookerId(booker.getId(), "PAST", Pageable.unpaged());

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(lastBooking.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(lastBooking.getItem().getName()));
    }

    @Test
    void getAllByBookerIdStatusFutureTest() {
        when(bookingRepository.findAllByStartAfterAndBookerIdOrderByStartDesc(
                any(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(nextBooking)));

        List<BookingDto> result = bookingService.getAllByBookerId(booker.getId(), "FUTURE", Pageable.unpaged());

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(nextBooking.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }


    @Test
    void getAllByBookerIdStatusWaitingTest() {
        when(bookingRepository.findAllByStatusAndBookerIdOrderByStartDesc(
                any(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(nextBooking)));

        List<BookingDto> result = bookingService.getAllByBookerId(booker.getId(), "WAITING", Pageable.unpaged());

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(nextBooking.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllByBookerIdStatusRejectedTest() {
        nextBooking.setStatus(Status.REJECTED);
        when(bookingRepository.findAllByStatusAndBookerIdOrderByStartDesc(
                any(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(nextBooking)));

        List<BookingDto> result = bookingService.getAllByBookerId(booker.getId(), "REJECTED", Pageable.unpaged());

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(nextBooking.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllByBookerIdStatusWrongTest() {
        Exception ex = assertThrows(IncorrectStateException.class, () ->
                bookingService.getAllByBookerId(booker.getId(), "DEFAULT", Pageable.unpaged()));
        assertEquals("Unknown state: DEFAULT", ex.getMessage());
    }

    @Test
    void getAllByOwnerIdStatusAllTest() {
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(nextBooking, lastBooking)));

        List<BookingDto> result = bookingService.getAllByOwnerId(booker.getId(), "ALL", Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.get(0).getId(), equalTo(nextBooking.getId()));
        assertThat(result.get(1).getItem().getName(), equalTo(lastBooking.getItem().getName()));
    }

    @Test
    void getAllByOwnerIdStatusCurrentTest() {
        Booking currentBooking = Booking.builder()
                .id(3)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findAllByStartBeforeAndEndAfterAndItemOwnerIdOrderByStartDesc(
                any(), any(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(currentBooking)));

        List<BookingDto> result = bookingService.getAllByOwnerId(booker.getId(), "CURRENT", Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.get(0).getId(), equalTo(currentBooking.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(currentBooking.getItem().getName()));

    }

    @Test
    void getAllByOwnerIdStatusPastTest() {
        when(bookingRepository.findAllByEndBeforeAndItemOwnerIdOrderByStartDesc(
                any(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(lastBooking)));

        List<BookingDto> result = bookingService.getAllByOwnerId(booker.getId(), "PAST", Pageable.unpaged());

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(lastBooking.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(lastBooking.getItem().getName()));
    }

    @Test
    void getAllByOwnerIdStatusFutureTest() {
        when(bookingRepository.findAllByStartAfterAndItemOwnerIdOrderByStartDesc(
                any(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(nextBooking)));

        List<BookingDto> result = bookingService.getAllByOwnerId(booker.getId(), "FUTURE", Pageable.unpaged());

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(nextBooking.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllByOwnerIdStatusWaitingTest() {
        when(bookingRepository.findAllByStatusAndItemOwnerIdOrderByStartDesc(
                any(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(nextBooking)));

        List<BookingDto> result = bookingService.getAllByOwnerId(booker.getId(), "WAITING", Pageable.unpaged());

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(nextBooking.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllByOwnerIdStatusRejectedTest() {
        nextBooking.setStatus(Status.REJECTED);
        when(bookingRepository.findAllByStatusAndItemOwnerIdOrderByStartDesc(
                any(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(nextBooking)));

        List<BookingDto> result = bookingService.getAllByOwnerId(booker.getId(), "REJECTED", Pageable.unpaged());

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(nextBooking.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllByOwnerIdStatusWrongTest() {
        Exception ex = assertThrows(IncorrectStateException.class, () ->
                bookingService.getAllByOwnerId(booker.getId(), "CANCELED", Pageable.unpaged()));
        assertEquals("Unknown state: CANCELED", ex.getMessage());
    }
}