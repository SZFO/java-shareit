package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "created");
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID) int userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Вызван метод create() в ItemRequestController пользователем с id {}.", userId);
        ItemRequestDto create = itemRequestService.save(userId, itemRequestDto);

        return ResponseEntity.ok().body(create).getBody();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getById(@RequestHeader(USER_ID) int userId,
                                @PathVariable int requestId) {
        log.info("Вызван метод getById() в ItemRequestController пользователем с id {} о запросе с id {}.",
                userId, requestId);
        ItemRequestDtoOut getById = itemRequestService.getById(userId, requestId);

        return ResponseEntity.ok().body(getById).getBody();
    }

    @GetMapping
    public List<ItemRequestDtoOut> getAll(@RequestHeader(USER_ID) int userId,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getAll() в ItemRequestController пользователем с id {}, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", userId, from, size);
        Pageable pageable = PageRequest.of(from / size, size, DEFAULT_SORT);
        List<ItemRequestDtoOut> getAll = itemRequestService.getAll(userId, pageable);

        return ResponseEntity.ok().body(getAll).getBody();
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllFromOtherUser(@RequestHeader(USER_ID) int userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getAllFromOtherUser() в ItemRequestController пользователем с id {}, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", userId, from, size);
        Pageable pageable = PageRequest.of(from / size, size, DEFAULT_SORT);
        List<ItemRequestDtoOut> getAllFromOtherUser = itemRequestService.getAllFromOtherUser(userId, pageable);

        return ResponseEntity.ok().body(getAllFromOtherUser).getBody();
    }
}