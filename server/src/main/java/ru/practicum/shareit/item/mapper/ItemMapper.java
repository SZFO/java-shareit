package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public static ItemDto itemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }

    public static Item dtoToItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(owner)
                .available(itemDto.getAvailable())
                .request(null)
                .build();
    }

    public static ItemDtoWithBooking itemWithBookingToDto(Item item) {
        return ItemDtoWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
    }

    public static List<ItemDto> toDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }
}