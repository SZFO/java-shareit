package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(int userId, BookingRequestDto bookingRequestDto);

    BookingDto getById(int id, int userId);

    List<BookingDto> getAllByBookerId(int bookerId, String state, Pageable pageable);

    List<BookingDto> getAllByOwnerId(int ownerId, String state, Pageable pageable);

    BookingDto approve(int bookingId, int ownerId, boolean approved);
}