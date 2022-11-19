package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(int requesterId, ItemRequestDto itemRequestDto);

    ItemRequestDtoOut findById(int userId, int requestId);

    List<ItemRequestDtoOut> findAll(int userId, int from, int size);

    List<ItemRequestDtoOut> findAllFromOtherUser(int userId, int from, int size);
}