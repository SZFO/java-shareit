package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    private Long id;

    private String text;

    private ItemDto itemDto;

    private String authorName;

    private LocalDateTime created;
}