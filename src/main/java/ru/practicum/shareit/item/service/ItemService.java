package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBooking> getAllByOwner(int ownerId);

    ItemDtoWithBooking getById(int userId, int id);

    ItemDto create(ItemDto itemDto, int ownerId);

    ItemDto update(ItemDto item, int userId, int id);

    void delete(int id);

    List<ItemDto> search(String query);

    CommentDto createComment(int userId, int itemId, CommentDto commentDto);
}