package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(Long requesterId, ItemRequestDto itemRequestDto);

    ItemRequestDtoOut getById(Long userId, Long requestId);

    List<ItemRequestDtoOut> getAll(Long userId);

    List<ItemRequestDtoOut> getAllFromOtherUser(Long userId, Pageable pageable);
}