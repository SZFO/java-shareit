package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    private int id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private User owner;

    @NotNull
    private Boolean available;

    private ItemRequest request;
}