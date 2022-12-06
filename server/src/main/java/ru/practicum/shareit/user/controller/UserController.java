package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Вызван метод getAll() в UserController.");
        List<UserDto> getAll = userService.getAll();

        return ResponseEntity.ok().body(getAll).getBody();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Вызван метод getById() в UserController для пользователя с id {}.", id);
        UserDto getById = userService.getById(id);

        return ResponseEntity.ok().body(getById).getBody();
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Вызван метод create() в UserController.");
        UserDto create = userService.create(userDto);

        return ResponseEntity.ok().body(create).getBody();
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable Long id) {
        log.info("Вызван метод update() в UserController для пользователя с id {}.", id);
        UserDto update = userService.update(userDto, id);

        return ResponseEntity.ok().body(update).getBody();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
        log.info("Вызван метод delete() в UserController для пользователя с id {}.", id);
        userService.delete(id);

        return ResponseEntity.ok().build();
    }
}