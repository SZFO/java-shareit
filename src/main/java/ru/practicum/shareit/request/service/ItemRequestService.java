package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(int requesterId, ItemRequestDto itemRequestDto);

    ItemRequestDtoOut getById(int userId, int requestId);

    List<ItemRequestDtoOut> getAll(int userId, Pageable pageable);

    List<ItemRequestDtoOut> getAllFromOtherUser(int userId, Pageable pageable);
}