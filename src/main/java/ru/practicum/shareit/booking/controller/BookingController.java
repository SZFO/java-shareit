package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID) int userId,
                             @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        log.info("Вызван метод create() в BookingController пользователем с id {}.", userId);
        BookingDto create = bookingService.save(userId, bookingRequestDto);

        return ResponseEntity.ok().body(create).getBody();
    }

    @PatchMapping("{bookingId}")
    public BookingDto approve(@RequestHeader(USER_ID) int userId,
                              @PathVariable int bookingId,
                              @RequestParam boolean approved) {
        log.info("Вызван метод approve() в BookingController пользователем с id {} для бронирования с id {}.",
                userId, bookingId);
        BookingDto approve = bookingService.approve(bookingId, userId, approved);

        return ResponseEntity.ok().body(approve).getBody();
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(USER_ID) int userId,
                               @PathVariable int bookingId) {
        log.info("Вызван метод findById() в BookingController для бронирования с id {}.", bookingId);
        BookingDto findById = bookingService.findById(bookingId, userId);

        return ResponseEntity.ok().body(findById).getBody();
    }

    @GetMapping
    public List<BookingDto> findByBookerId(@RequestHeader(USER_ID) int bookerId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        log.info("Вызван метод findByBookerId() в BookingController для пользователя с id {}.", bookerId);
        List<BookingDto> findByBookerId = bookingService.findAllByBookerId(bookerId, state);

        return ResponseEntity.ok().body(findByBookerId).getBody();
    }

    @GetMapping("/owner")
    public List<BookingDto> findByOwnerId(@RequestHeader(USER_ID) int ownerId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        log.info("Вызван метод findByOwnerId() в BookingController для пользователя с id {}.", ownerId);
        List<BookingDto> findByOwnerId = bookingService.findAllByOwnerId(ownerId, state);

        return ResponseEntity.ok().body(findByOwnerId).getBody();
    }
}