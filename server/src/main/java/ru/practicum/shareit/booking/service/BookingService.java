package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(Long userId, BookingRequestDto bookingRequestDto);

    BookingDto getById(Long id, Long userId);

    List<BookingDto> getAllByBookerId(Long bookerId, String state, Pageable pageable);

    List<BookingDto> getAllByOwnerId(Long ownerId, String state, Pageable pageable);

    BookingDto approve(Long bookingId, Long ownerId, boolean approved);
}