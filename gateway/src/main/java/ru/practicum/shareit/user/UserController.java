package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Вызван метод getAll() в UserController.");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable long userId) {
        log.info("Вызван метод getById() в UserController для пользователя с id {}.", userId);
        return userClient.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class})
                                         @RequestBody UserDto userDto) {
        log.info("Вызван метод create() в UserController.");
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Validated({Update.class})
                                         @PathVariable long userId,
                                         @RequestBody UserDto userDto) {
        log.info("Вызван метод update() в UserController для пользователя с id {}.", userId);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        log.info("Вызван метод delete() в UserController для пользователя с id {}.", userId);
        return userClient.delete(userId);
    }
}