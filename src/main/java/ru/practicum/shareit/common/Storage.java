package ru.practicum.shareit.common;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {
    List<T> getAll();

    Optional<T> getById(int id);

    T create(T item);

    T update(T item);

    void delete(int id);
}
