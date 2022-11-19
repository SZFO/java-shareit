package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Вызван метод getAll() в UserController.");
        List<UserDto> findAll = userService.findAll();

        return ResponseEntity.ok().body(findAll).getBody();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable int id) {
        log.info("Вызван метод getById() в UserController для пользователя с id {}.", id);
        UserDto findById = userService.findById(id);

        return ResponseEntity.ok().body(findById).getBody();
    }

    @PostMapping
    public UserDto create(@Validated({Create.class})
                          @RequestBody UserDto userDto) {
        log.info("Вызван метод create() в UserController.");
        UserDto create = userService.create(userDto);

        return ResponseEntity.ok().body(create).getBody();
    }

    @PatchMapping("/{id}")
    public UserDto update(@Validated({Update.class})
                          @RequestBody UserDto userDto,
                          @PathVariable int id) {
        log.info("Вызван метод update() в UserController для пользователя с id {}.", id);
        UserDto update = userService.update(userDto, id);

        return ResponseEntity.ok().body(update).getBody();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable int id) {
        log.info("Вызван метод delete() в UserController для пользователя с id {}.", id);
        userService.delete(id);

        return ResponseEntity.ok().build();
    }
}