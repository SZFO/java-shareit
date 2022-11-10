package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@Builder
public class BookingDto {
    private int id;

    private Item item;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;

    private User booker;

    @Enumerated(EnumType.STRING)
    private Status status;
}