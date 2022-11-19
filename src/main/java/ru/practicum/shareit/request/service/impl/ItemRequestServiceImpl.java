package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.*;
import static ru.practicum.shareit.user.mapper.UserMapper.dtoToUser;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "created");

    private final UserService userService;

    private final ItemRequestRepository itemRequestRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto save(int requesterId, ItemRequestDto itemRequestDto) {
        User requester = dtoToUser(userService.findById(requesterId));
        ItemRequest itemRequest = dtoToItemRequest(itemRequestDto, requester, LocalDateTime.now());

        return itemRequestToDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDtoOut findById(int userId, int requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %s пользователя " +
                        "с id = %s не найден.", requestId, userId)));

        return requestToOut(itemRequest, userId);
    }

    @Override
    public List<ItemRequestDtoOut> findAll(int userId, int from, int size) {
        userService.findById(userId);
        Page<ItemRequest> requests = itemRequestRepository
                .findAllByRequesterId(userId, PageRequest.of(from / size, size, DEFAULT_SORT));

        return requests.stream()
                .map(a -> requestToOut(a, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoOut> findAllFromOtherUser(int userId, int from, int size) {
        userService.findById(userId);
        Page<ItemRequest> requests = itemRequestRepository
                .findAllByRequesterIdIsNot(userId, PageRequest.of(from / size, size, DEFAULT_SORT));

        return requests.stream()
                .map(a -> requestToOut(a, userId))
                .collect(Collectors.toList());
    }

    private ItemRequestDtoOut requestToOut(ItemRequest itemRequest, int userId) {
        userService.findById(userId);
        List<ItemDto> items = toDtoList(itemRepository.findByRequestId(itemRequest.getId()));

        return itemRequestToDtoOut(itemRequest, items);
    }
}