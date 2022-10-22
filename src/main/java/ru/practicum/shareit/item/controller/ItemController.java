package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(USER_ID) int ownerId) {
        log.info("Вызван метод getAll() в ItemController.");

        return ResponseEntity.ok().body(itemService.getAllByOwner(ownerId)).getBody();
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable int id) {
        log.info("Вызван метод getById() в ItemController для вещи с id {}.", id);

        return ResponseEntity.ok().body(itemService.getById(id)).getBody();
    }

    @PostMapping
    public ItemDto create(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID) int ownerId) {
        log.info("Вызван метод create() в ItemController для владельца c id {}.", ownerId);

        return ResponseEntity.ok().body(itemService.create(itemDto, ownerId)).getBody();
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID) int ownerId, @PathVariable int itemId) {
        log.info("Вызван метод update() в ItemController для владельца с id {} и вещи с id {}.", ownerId, itemId);

        return ResponseEntity.ok().body(itemService.update(itemDto, ownerId, itemId)).getBody();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable int id) {
        log.info("Вызван метод delete() в ItemController для вещи с id {}.", id);
        itemService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Вызван метод search() в ItemController для поиска вещи по тексту {}.", text);

        return ResponseEntity.ok().body(itemService.search(text)).getBody();
    }
}