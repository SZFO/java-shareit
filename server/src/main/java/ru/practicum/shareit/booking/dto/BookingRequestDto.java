package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingRequestDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;
}