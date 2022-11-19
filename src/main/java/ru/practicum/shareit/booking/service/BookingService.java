package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(int userId, BookingRequestDto bookingRequestDto);

    BookingDto findById(int id, int userId);

    List<BookingDto> findAllByBookerId(int bookerId, String state, int from, int size);

    List<BookingDto> findAllByOwnerId(int ownerId, String state, int from, int size);

    BookingDto approve(int bookingId, int ownerId, boolean approved);
}