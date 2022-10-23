package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllByOwner(int ownerId);

    ItemDto getById(int id);

    ItemDto create(ItemDto itemDto, int ownerId);

    ItemDto update(ItemDto item, int userId, int id);

    void delete(int id);

    List<ItemDto> search(String query);
}