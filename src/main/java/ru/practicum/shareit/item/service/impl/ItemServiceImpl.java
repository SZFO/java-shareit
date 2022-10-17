package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getAllByOwner(int ownerId) {
        return itemRepository.getAll().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(int id) {
        return ItemMapper.itemToDto(itemRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена.", id))));
    }

    @Override
    public ItemDto create(ItemDto itemDto, int ownerId) {
        User owner = userRepository.getById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден.", ownerId)));
        Item item = ItemMapper.dtoToItem(itemDto, owner);

        return ItemMapper.itemToDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, int userId, int itemId) {
        User owner = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден.", userId)));
        Item oldItem = itemRepository.getById(itemId).get();
        if (oldItem.getOwner() == owner) {
            Item updatedItem = itemRepository.getById(itemId)
                    .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена.", itemId)));
            if (itemDto.getName() != null) {
                updatedItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                updatedItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                updatedItem.setAvailable(itemDto.getAvailable());
            }
            return ItemMapper.itemToDto(itemRepository.update(updatedItem));
        } else {
            throw new NotFoundException("Редактировать информацию о вещи может только ее владелец.");
        }
    }

    @Override
    public void delete(int id) {
        itemRepository.delete(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.getAll().stream()
                .filter(item -> validateSearch(item, text))
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    private boolean validateSearch(Item item, String text) {
        return (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase())) &&
                item.getAvailable().equals(true);
    }
}