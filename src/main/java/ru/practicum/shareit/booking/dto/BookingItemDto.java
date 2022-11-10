package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class BookingItemDto {
    private int id;

    private int bookerId;
}