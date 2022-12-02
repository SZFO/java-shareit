package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Вызван метод create() в ItemRequestController пользователем с id {}.", userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID) int userId,
                                          @PathVariable int requestId) {
        log.info("Вызван метод getById() в ItemRequestController пользователем с id {} о запросе с id {}.",
                userId, requestId);
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID) int userId) {
        log.info("Вызван метод getAll() в ItemRequestController пользователем с id {}", userId);
        return itemRequestClient.getAll(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllFromOtherUser(@RequestHeader(USER_ID) int userId,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                      @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getAllFromOtherUser() в ItemRequestController пользователем с id {}, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", userId, from, size);
        return itemRequestClient.getAllFromOtherUser(userId, from, size);
    }
}