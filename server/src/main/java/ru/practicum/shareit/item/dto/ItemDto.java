package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}