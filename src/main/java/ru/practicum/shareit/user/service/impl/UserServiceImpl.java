package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

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

        return UserMapper.userToDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        validateEmail(user.getEmail());

        return UserMapper.userToDto(userRepository.create(user));
    }

    @Override
    public UserDto update(UserDto userDto, int id) {
        User updatedUser = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден.", id)));
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
            updatedUser.setEmail(userDto.getEmail());
        }
        return UserMapper.userToDto(userRepository.update(updatedUser));
    }

    @Override
    public void delete(int id) {
        userRepository.delete(id);
    }

    private void validateEmail(String email) {
        if (userRepository.getAll().stream()
                .anyMatch(a -> a.getEmail().toLowerCase(Locale.ROOT).equals(email.toLowerCase()))) {
            throw new ValidateException(String.format("Пользователь с почтой %s уже существует.", email));
        }
    }
}