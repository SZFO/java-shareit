package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.mapper.*;
import ru.practicum.shareit.item.repository.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.item.mapper.CommentMapper.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.itemWithBookingToDto;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;


    @Override
    public List<ItemDtoWithBooking> getAllByOwner(int userId) {
        return itemRepository.findByOwnerIdOrderByIdAsc(userId).stream()
                .map(ItemMapper::itemWithBookingToDto)
                .map(this::createLastAndNextBooking)
                .peek(itemDto -> itemDto.setComments(toDtoList(commentRepository.findAllByItemId(itemDto.getId()))))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoWithBooking getById(int userId, int id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена.", id)));
        ItemDtoWithBooking itemDtoWithBooking = itemWithBookingToDto(item);
        if (item.getOwner().getId() == userId) {
            createLastAndNextBooking(itemDtoWithBooking);
        }
        itemDtoWithBooking.setComments(CommentMapper.toDtoList(commentRepository.findAllByItemId(item.getId())));
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
        List<Item> items = itemRepository.searchAvailableItems(text);
        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(int userId, int itemId, CommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new BadRequestException("Комментарий не должен быть пустым.");
        }
        boolean bookingBoolean = bookingRepository.findBookingsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).isEmpty();
        if (bookingBoolean) {
            throw new BadRequestException(String.format("Пользователь с id = %s не осуществлял бронирование " +
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

    private ItemDtoWithBooking createLastAndNextBooking(ItemDtoWithBooking itemDtoWithBooking) {
        LocalDateTime now = LocalDateTime.now();
        bookingRepository.findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(itemDtoWithBooking.getId(), now)
                .ifPresent(booking -> itemDtoWithBooking.setLastBooking(bookingItemToDto(booking)));
        bookingRepository.findBookingsByItemIdAndStartIsAfterOrderByStartDesc(itemDtoWithBooking.getId(), now)
                .ifPresent(booking -> itemDtoWithBooking.setNextBooking(bookingItemToDto(booking)));
        return itemDtoWithBooking;
    }
}