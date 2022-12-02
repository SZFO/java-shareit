package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class BookingItemDto {
    private Long id;

    private Long bookerId;
}