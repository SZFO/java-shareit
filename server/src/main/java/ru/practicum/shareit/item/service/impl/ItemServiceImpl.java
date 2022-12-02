package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.mapper.*;
import ru.practicum.shareit.item.repository.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
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

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDtoWithBooking> getAllByOwner(Long userId, Pageable pageable) {
        Page<Item> itemsDto = itemRepository.findByOwnerId(userId, pageable);

        return itemsDto.stream()
                .map(ItemMapper::itemWithBookingToDto)
                .map(this::createLastAndNextBooking)
                .peek(itemDto -> itemDto.setComments(toDtoList(commentRepository.findAllByItemId(itemDto.getId()))))
                .collect(toList());
    }

    @Override
    public ItemDtoWithBooking getById(Long userId, Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена.", id)));
        ItemDtoWithBooking itemDtoWithBooking = itemWithBookingToDto(item);
        if (Objects.equals(item.getOwner().getId(), userId)) {
            createLastAndNextBooking(itemDtoWithBooking);
        }
        itemDtoWithBooking.setComments(CommentMapper.toDtoList(commentRepository.findAllByItemId(item.getId())));

        return itemDtoWithBooking;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден.", ownerId)));
        Item item = dtoToItem(itemDto, owner);
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(String.format("Запрос на вещь с id = %s не найден",
                            itemDto.getRequestId()))));
        }

        return itemToDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена.", itemId)));
        if (!Objects.equals(userId, updatedItem.getOwner().getId())) {
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
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String text, Pageable pageable) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Page<Item> items = itemRepository
                .searchAvailableItems(text, pageable);

        return toDtoList(items.stream()
                .collect(toList()));
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new BadRequestException("Комментарий не должен быть пустым.");
        }
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, Status.APPROVED,
                        LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException(String.format("Пользователь с id = %s не осуществлял бронирование " +
                    "вещи с id = %s", userId, itemId));
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id = %s не найден.", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с id = %s не найдена.", itemId)));

        Comment comment = dtoToComment(commentDto, item, user, LocalDateTime.now());
        Comment commentSave = commentRepository.save(comment);

        return commentToDto(commentSave);
    }

    private ItemDtoWithBooking createLastAndNextBooking(ItemDtoWithBooking itemDtoWithBooking) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> lastBookings = bookingRepository
                .findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(itemDtoWithBooking.getId(), now);
        List<Booking> nextBookings = bookingRepository
                .findBookingsByItemIdAndStartIsAfterOrderByStartDesc(itemDtoWithBooking.getId(), now);
        if (!lastBookings.isEmpty()) {
            BookingItemDto lastBooking = bookingItemToDto(lastBookings.get(0));
            itemDtoWithBooking.setLastBooking(lastBooking);
        }
        if (!nextBookings.isEmpty()) {
            BookingItemDto nextBooking = bookingItemToDto(nextBookings.get(0));
            itemDtoWithBooking.setNextBooking(nextBooking);
        }

        return itemDtoWithBooking;
    }
}