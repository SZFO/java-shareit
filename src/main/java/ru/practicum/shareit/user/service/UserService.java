package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(int id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, int id);

    void delete(int id);
}