package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID) long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getAllByOwner() в ItemController для пользователя с id {}, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", userId, from, size);

        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID) long userId,
                                          @PathVariable long itemId) {
        log.info("Вызван метод getById() в ItemController для вещи с id {}.", itemId);

        return itemClient.getById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class})
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader(USER_ID) long userId) {
        log.info("Вызван метод create() в ItemController для владельца c id {}.", userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Validated({Update.class})
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader(USER_ID) long userId,
                                         @PathVariable long itemId) {
        log.info("Вызван метод update() в ItemController для владельца с id {} и вещи с id {}.", userId, itemId);
        return itemClient.update(itemDto, userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@PathVariable long itemId) {
        log.info("Вызван метод delete() в ItemController для вещи с id {}.", itemId);
        return itemClient.delete(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_ID) long userId,
                                         @RequestParam String text,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод search() в ItemController для поиска вещи по тексту {}, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", text, from, size);
        return itemClient.search(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Validated({Create.class})
                                                @RequestBody CommentDto commentDto,
                                                @RequestHeader(USER_ID) long userId,
                                                @PathVariable long itemId) {
        log.info("Вызван метод createComment() в ItemController пользователем с id {} для вещи с id {}.",
                userId, itemId);

        return itemClient.createComment(userId, itemId, commentDto);
    }
}