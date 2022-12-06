package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "created");

    private static final String USER_ID = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Вызван метод create() в ItemRequestController пользователем с id {}.", userId);
        ItemRequestDto create = itemRequestService.save(userId, itemRequestDto);

        return ResponseEntity.ok().body(create).getBody();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getById(@RequestHeader(USER_ID) Long userId,
                                     @PathVariable Long requestId) {
        log.info("Вызван метод getById() в ItemRequestController пользователем с id {} о запросе с id {}.",
                userId, requestId);
        ItemRequestDtoOut getById = itemRequestService.getById(userId, requestId);

        return ResponseEntity.ok().body(getById).getBody();
    }

    @GetMapping
    public List<ItemRequestDtoOut> getAll(@RequestHeader(USER_ID) Long userId) {
        log.info("Вызван метод getAll() в ItemRequestController пользователем с id {}", userId);
        List<ItemRequestDtoOut> getAll = itemRequestService.getAll(userId);

        return ResponseEntity.ok().body(getAll).getBody();
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllFromOtherUser(@RequestHeader(USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getAllFromOtherUser() в ItemRequestController пользователем с id {}, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", userId, from, size);
        Pageable pageable = PageRequest.of(from / size, size, DEFAULT_SORT);
        List<ItemRequestDtoOut> getAllFromOtherUser = itemRequestService.getAllFromOtherUser(userId, pageable);

        return ResponseEntity.ok().body(getAllFromOtherUser).getBody();
    }
}