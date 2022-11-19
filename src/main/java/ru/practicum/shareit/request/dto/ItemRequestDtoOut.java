package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemRequestDtoOut {
    private int id;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    private LocalDateTime created;

    private List<ItemDto> items;
}