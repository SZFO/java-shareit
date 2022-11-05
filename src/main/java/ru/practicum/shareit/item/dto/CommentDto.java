package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    private Integer id;

    private String text;

    private String authorName;

    private LocalDateTime created;
}