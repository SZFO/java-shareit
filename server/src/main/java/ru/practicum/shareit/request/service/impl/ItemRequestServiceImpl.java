package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.*;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.dtoToItemRequest;
import static ru.practicum.shareit.user.mapper.UserMapper.dtoToUser;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;

    private final ItemRequestRepository itemRequestRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto save(Long requesterId, ItemRequestDto itemRequestDto) {
        ItemRequest saved = itemRequestRepository.save(
                dtoToItemRequest(itemRequestDto, dtoToUser(userService.getById(requesterId))));

        return itemRequestToDto(saved);
    }

    @Override
    public ItemRequestDtoOut getById(Long userId, Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %s пользователя " +
                        "с id = %s не найден.", requestId, userId)));

        return requestToOut(itemRequest, userId);
    }

    @Override
    public List<ItemRequestDtoOut> getAll(Long userId) {
        userService.getById(userId);

        return itemRequestRepository.findAllByRequesterId(userId).stream()
                .map(a -> requestToOut(a, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoOut> getAllFromOtherUser(Long userId, Pageable pageable) {
        userService.getById(userId);
        Page<ItemRequest> requests = itemRequestRepository
                .findAllByRequesterIdIsNot(userId, pageable);

        return requests.stream()
                .map(a -> requestToOut(a, userId))
                .collect(Collectors.toList());
    }

    private ItemRequestDtoOut requestToOut(ItemRequest itemRequest, Long userId) {
        userService.getById(userId);
        List<ItemDto> items = toDtoList(itemRepository.findByRequestId(itemRequest.getId()));

        return itemRequestToDtoOut(itemRequest, items);
    }
}