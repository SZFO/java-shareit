package ru.practicum.shareit.item.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> items;

    private int id = 1;

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> getById(int id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item create(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item update(Item newItem) {
        items.put(newItem.getId(), newItem);

        return newItem;
    }

    @Override
    public void delete(int id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException(String.format("Предмет с id = %s не найден.", id));
        }
        items.remove(id);
    }

    private int getNextId() {
        return id++;
    }
}