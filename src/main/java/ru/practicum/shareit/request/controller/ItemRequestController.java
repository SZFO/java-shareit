package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ItemRequestService itemRequestService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID) int userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Вызван метод create() в ItemRequestController пользователем с id {}.", userId);
        ItemRequestDto create = itemRequestService.save(userId, itemRequestDto);

        return ResponseEntity.ok().body(create).getBody();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut findById(@RequestHeader(USER_ID) int userId,
                                @PathVariable int requestId) {
        log.info("Вызван метод findById() в ItemRequestController пользователем с id {} о запросе с id {}.",
                userId, requestId);
        ItemRequestDtoOut findById = itemRequestService.findById(userId, requestId);

        return ResponseEntity.ok().body(findById).getBody();
    }

    @GetMapping
    public List<ItemRequestDtoOut> findAll(@RequestHeader(USER_ID) int userId,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод findAll() в ItemRequestController пользователем с id {}, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", userId, from, size);
        List<ItemRequestDtoOut> findAll = itemRequestService.findAll(userId, from, size);

        return ResponseEntity.ok().body(findAll).getBody();
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> findAllFromOtherUser(@RequestHeader(USER_ID) int userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод findAllFromOtherUser() в ItemRequestController пользователем с id {}, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", userId, from, size);
        List<ItemRequestDtoOut> findAllFromOtherUser = itemRequestService.findAllFromOtherUser(userId, from, size);

        return ResponseEntity.ok().body(findAllFromOtherUser).getBody();
    }
}