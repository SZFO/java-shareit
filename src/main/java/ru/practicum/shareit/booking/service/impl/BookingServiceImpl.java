package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
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
    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public BookingDto save(int userId, BookingRequestDto bookingRequestDto) {
        Booking booking = dtoRequestToBooking(bookingRequestDto);
        booking.setBooker(dtoToUser(userService.getById(userId)));
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
        if (item.getAvailable()) {
            booking.setItem(item);
            Booking bookingCreate = bookingRepository.save(booking);

            return bookingToDto(bookingCreate);
        } else {
            throw new BadRequestException(
                    String.format("Вещь с id = %s недоступна для бронирования.", item.getId()));
        }
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
    public List<BookingDto> findAllByBookerId(int bookerId, String state) {
        throwNotValidState(state);
        userService.getById(bookerId);
        List<Booking> bookings = null;
        LocalDateTime now = LocalDateTime.now();
        switch (BookingState.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByStartBeforeAndEndAfterAndBooker_IdOrderByStartDesc(now,
                        now, bookerId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByEndBeforeAndBooker_IdOrderByStartDesc(now, bookerId);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByStartAfterAndBooker_IdOrderByStartDesc(now, bookerId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByStatusAndBooker_IdOrderByStartDesc(Status.WAITING, bookerId);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByStatusAndBooker_IdOrderByStartDesc(Status.REJECTED, bookerId);
                break;
        }

        return bookings.stream().map(BookingMapper::bookingToDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwnerId(int ownerId, String state) {
        throwNotValidState(state);
        userService.getById(ownerId);
        List<Booking> bookings = null;
        LocalDateTime now = LocalDateTime.now();
        switch (BookingState.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByStartBeforeAndEndAfterAndItem_Owner_IdOrderByStartDesc(now,
                        now, ownerId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByEndBeforeAndItem_Owner_IdOrderByStartDesc(now, ownerId);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByStartAfterAndItem_Owner_IdOrderByStartDesc(now, ownerId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByStatusAndItem_Owner_IdOrderByStartDesc(Status.WAITING, ownerId);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByStatusAndItem_Owner_IdOrderByStartDesc(Status.REJECTED, ownerId);
                break;
        }

        return bookings.stream().map(BookingMapper::bookingToDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto approve(int bookingId, int ownerId, boolean approved) {
        BookingDto bookingDto = bookingToDto(bookingRepository.findById(bookingId).orElseThrow());
        Booking booking = dtoToBooking(bookingDto);
        if (ownerId != bookingDto.getItem().getOwner().getId()) {
            throw new NotFoundException("Подтвердить бронирование может только владелец вещи");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Бронирование уже было подтверждено");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            Booking bookingSave = bookingRepository.save(booking);

            return bookingToDto(bookingSave);
        } else {
            booking.setStatus(Status.REJECTED);
            booking.setItem(bookingDto.getItem());
            Booking bookingSave = bookingRepository.save(booking);

            return bookingToDto(bookingSave);
        }
    }

    private void throwNotValidState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }
}