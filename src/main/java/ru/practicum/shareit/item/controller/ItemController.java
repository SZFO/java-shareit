package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
        log.info("Вызван метод getAll() в ItemController");

        return itemService.getAllByOwner(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable int id) {
        log.info("Вызван метод getById() в ItemController");

        return itemService.getById(id);
    }

    @PostMapping
    public ItemDto create(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID) int ownerId) {
        log.info("Вызван метод create() в ItemController");

        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID) int ownerId,
                          @PathVariable int itemId,
                          @Valid @NotNull @RequestBody ItemDto itemDto) {
        log.info("Вызван метод update() в ItemController");

        return itemService.update(itemDto, ownerId, itemId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable int id) {
        log.info("Вызван метод delete() в ItemController");
        itemService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Вызван метод search() в ItemController");

        return itemService.search(text);
    }
}