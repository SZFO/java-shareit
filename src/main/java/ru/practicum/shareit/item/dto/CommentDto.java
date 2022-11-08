package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    private Integer id;

    @NotBlank(groups = {Create.class})
    private String text;

    private String authorName;

    private LocalDateTime created;
}