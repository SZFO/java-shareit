package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.Storage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Storage<User> userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(int id) {
        User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден.", id)));

        return userToDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = dtoToUser(userDto);
        throwCrossEmail(user.getEmail());

        return userToDto(userRepository.create(user));
    }

    @Override
    public UserDto update(UserDto userDto, int id) {
        User updatedUser = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден.", id)));
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            throwCrossEmail(userDto.getEmail());
            updatedUser.setEmail(userDto.getEmail());
        }
        return userToDto(userRepository.update(updatedUser));


    }

    @Override
    public void delete(int id) {
        userRepository.delete(id);
    }

    private void throwCrossEmail(String email) {
        if (userRepository.getAll().stream().anyMatch(a -> a.getEmail().equalsIgnoreCase(email))) {
            throw new ConflictException(String.format("Пользователь с почтой %s уже существует.", email));
        }
    }
}