package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.IncorrectStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.*;
import static ru.practicum.shareit.user.mapper.UserMapper.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "start");

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public BookingDto save(int userId, BookingRequestDto bookingRequestDto) {
        Booking booking = dtoRequestToBooking(bookingRequestDto);
        booking.setBooker(dtoToUser(userService.findById(userId)));
        Item item = getValidItemForBooking(bookingRequestDto, booking, userId);
        booking.setItem(item);
        Booking bookingCreate = bookingRepository.save(booking);

        return bookingToDto(bookingCreate);
    }

    @Override
    public BookingDto findById(int id, int userId) {
        Booking booking = bookingRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id = %s не существует.", id)));
        if ((booking.getBooker().getId() != (userId)) && (booking.getItem().getOwner().getId() != (userId))) {
            throw new NotFoundException(String.format("Пользователь с id = %s не осуществлял бронирование.", userId));
        }

        return bookingToDto(booking);
    }

    @Override
    public List<BookingDto> findAllByBookerId(int bookerId, String state, int from, int size) {
        throwNotValidState(state);
        userService.findById(bookerId);
        Page<Booking> bookings = null;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, DEFAULT_SORT);
        switch (BookingState.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByStartBeforeAndEndAfterAndBookerIdOrderByStartDesc(now, now,
                        bookerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByEndBeforeAndBookerIdOrderByStartDesc(now, bookerId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByStartAfterAndBookerIdOrderByStartDesc(now, bookerId, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByStatusAndBookerIdOrderByStartDesc(Status.WAITING, bookerId,
                        pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByStatusAndBookerIdOrderByStartDesc(Status.REJECTED, bookerId,
                        pageable);
                break;
        }

        return bookings.stream().map(BookingMapper::bookingToDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwnerId(int ownerId, String state, int from, int size) {
        throwNotValidState(state);
        userService.findById(ownerId);
        Page<Booking> bookings = null;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, DEFAULT_SORT);
        switch (BookingState.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByStartBeforeAndEndAfterAndItemOwnerIdOrderByStartDesc(now, now,
                        ownerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByEndBeforeAndItemOwnerIdOrderByStartDesc(now, ownerId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByStartAfterAndItemOwnerIdOrderByStartDesc(now, ownerId, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByStatusAndItemOwnerIdOrderByStartDesc(Status.WAITING, ownerId,
                        pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByStatusAndItemOwnerIdOrderByStartDesc(Status.REJECTED, ownerId,
                        pageable);
                break;
        }

        return bookings.stream().map(BookingMapper::bookingToDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto approve(int bookingId, int ownerId, boolean approved) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирования с id = %s не существует.",
                        bookingId)));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new NotFoundException("Подтвердить бронирование может только владелец вещи.");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Бронирование уже было подтверждено.");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);

        return bookingToDto(booking);
    }

    private void throwNotValidState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStateException(String.format("Unknown state: %s", state));
        }
    }

    private Item getValidItemForBooking(BookingRequestDto bookingRequestDto, Booking booking, int userId) {
        Item item = itemRepository
                .findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена.",
                        bookingRequestDto.getItemId())));
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец вещи не может забронировать свою вещь");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BadRequestException("Некорректное время окончания бронирования.");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Некорректное время начала бронирования.");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException(
                    String.format("Вещь с id = %s недоступна для бронирования.", item.getId()));
        }
        return item;
    }
}