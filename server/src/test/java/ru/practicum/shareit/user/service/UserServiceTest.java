package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(UserService.class)
@AutoConfigureMockMvc
class UserServiceTest {
    @MockBean
    UserServiceImpl userService;

    UserRepository userRepository;

    UserDto userDto;

    User user;

    @BeforeEach
    void init() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Вадим")
                .email("vadim@gmail.com")
                .build();

        user = User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        userRepository = mock(UserRepository.class);

        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAllTest() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> users = userService.getAll();

        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void getByIdTest() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UserDto result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void createTest() {
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void updateTest() {
        User updatedUser = User.builder()
                .id(1L)
                .name("Вадим Фаустов")
                .email("vadimfaustov@gmail.com")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(updatedUser.getId())
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(updatedUser);

        UserDto result = userService.update(updatedUserDto, user.getId());

        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
    }

    @Test
    void updateNullNameAndEmailTest() {
        User updatedUser = User.builder()
                .id(1L)
                .name(user.getName())
                .email(user.getEmail())
                .build();


        UserDto updatedUserDto = UserDto.builder()
                .id(updatedUser.getId())
                .name(null)
                .email(null)
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(updatedUser);

        UserDto result = userService.update(updatedUserDto, user.getId());

        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void delete() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.delete(userDto.getId());
        List<User> users = userRepository.findAll();

        assertEquals(0, users.size());
    }
}