package ru.practicum.shareit.user.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users;

    private int id = 1;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User newUser) {
        users.put(newUser.getId(), newUser);

        return newUser;
    }

    @Override
    public void delete(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователь с id = %s не найден.", id));
        }
        users.remove(id);
    }

    private int getNextId() {
        return id++;
    }
}