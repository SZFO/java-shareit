package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.mapper.*;
import ru.practicum.shareit.item.repository.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.item.mapper.CommentMapper.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    public List<ItemDtoWithBooking> getAllByOwner(int ownerId) {
        List<ItemDtoWithBooking> itemsDtoWithBookingList = itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemMapper::itemWithBookingToDto)
                .collect(Collectors.toList());
        for (ItemDtoWithBooking itemDtoWithBooking : itemsDtoWithBookingList) {
            createLastAndNextBooking(itemDtoWithBooking);
            List<Comment> comments = commentRepository.findAllByItemId(itemDtoWithBooking.getId());
            if (!comments.isEmpty()) {
                itemDtoWithBooking.setComments(comments
                        .stream().map(CommentMapper::commentToDto)
                        .collect(Collectors.toList()));
            }
        }
        itemsDtoWithBookingList.sort(Comparator.comparing(ItemDtoWithBooking::getId));

        return itemsDtoWithBookingList;
    }

    @Override
    public ItemDtoWithBooking getById(int userId, int id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена.", id)));
        ItemDtoWithBooking itemDtoWithBooking = itemWithBookingToDto(item);
        if (item.getOwner().getId() == userId) {
            createLastAndNextBooking(itemDtoWithBooking);
        }
        List<Comment> comments = commentRepository.findAllByItemId(id);
        if (!comments.isEmpty()) {
            itemDtoWithBooking.setComments(comments
                    .stream().map(CommentMapper::commentToDto)
                    .collect(Collectors.toList())
            );
        }
        return itemDtoWithBooking;
    }

    @Override
    public ItemDto create(ItemDto itemDto, int ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден.", ownerId)));
        Item item = dtoToItem(itemDto, owner);

        return itemToDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, int userId, int itemId) {
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена.", itemId)));
        if (userId != updatedItem.getOwner().getId()) {
            throw new NotFoundException("Редактировать информацию о вещи может только ее владелец.");
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        return itemToDto(itemRepository.save(updatedItem));
    }

    @Override
    public void delete(int id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAll().stream()
                .filter(item -> validateSearch(item, text))
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(int userId, int itemId, CommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Комментарий не должен быть пустым.");
        }
        boolean bookingBoolean = bookingRepository.findBookingsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).isEmpty();
        if (bookingBoolean) {
            throw new ValidationException(String.format("Пользователь с id = %s не осуществлял бронирование " +
                    "вещи с id = %s", userId, itemId));
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id = %s не найден.", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с id = %s не найдена.", itemId)));

        Comment comment = dtoToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        Comment commentSave = commentRepository.save(comment);

        return commentToDto(commentSave);
    }

    private boolean validateSearch(Item item, String text) {
        return (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase())) &&
                item.getAvailable().equals(true);
    }

    private void createLastAndNextBooking(ItemDtoWithBooking itemDtoWithBooking) {
        List<Booking> lastBookings = bookingRepository
                .findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(itemDtoWithBooking.getId(),
                        LocalDateTime.now());
        if (!lastBookings.isEmpty()) {
            BookingItemDto lastBooking = bookingItemToDto(lastBookings.get(0));
            itemDtoWithBooking.setLastBooking(lastBooking);
        }
        List<Booking> nextBookings = bookingRepository
                .findBookingsByItemIdAndStartIsAfterOrderByStartDesc(itemDtoWithBooking.getId(),
                        LocalDateTime.now());
        if (!nextBookings.isEmpty()) {
            BookingItemDto nextBooking = bookingItemToDto(nextBookings.get(0));
            itemDtoWithBooking.setNextBooking(nextBooking);
        }
    }
}