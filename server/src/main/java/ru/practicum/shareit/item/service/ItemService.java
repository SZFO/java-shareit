package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBooking> getAllByOwner(Long ownerId, Pageable pageable);

    ItemDtoWithBooking getById(Long userId, Long id);

    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(ItemDto item, Long userId, Long id);

    void delete(Long id);

    List<ItemDto> search(String query, Pageable pageable);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}