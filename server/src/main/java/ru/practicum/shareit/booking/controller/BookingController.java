package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "start");

    private static final String USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID) Long userId,
                             @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Вызван метод create() в BookingController пользователем с id {}.", userId);
        BookingDto create = bookingService.save(userId, bookingRequestDto);

        return ResponseEntity.ok().body(create).getBody();
    }

    @PatchMapping("{bookingId}")
    public BookingDto approve(@RequestHeader(USER_ID) Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam boolean approved) {
        log.info("Вызван метод approve() в BookingController пользователем с id {} для бронирования с id {}.",
                userId, bookingId);
        BookingDto approve = bookingService.approve(bookingId, userId, approved);

        return ResponseEntity.ok().body(approve).getBody();
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(USER_ID) Long userId,
                              @PathVariable Long bookingId) {
        log.info("Вызван метод getById() в BookingController для бронирования с id {}.", bookingId);
        BookingDto getById = bookingService.getById(bookingId, userId);

        return ResponseEntity.ok().body(getById).getBody();
    }

    @GetMapping
    public List<BookingDto> getByBookerId(@RequestHeader(USER_ID) Long bookerId,
                                          @RequestParam(defaultValue = "ALL") String state,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getByBookerId() в BookingController для пользователя с id {}, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", bookerId, from, size);
        Pageable pageable = PageRequest.of(from / size, size, DEFAULT_SORT);
        List<BookingDto> getByBookerId = bookingService.getAllByBookerId(bookerId, state, pageable);

        return ResponseEntity.ok().body(getByBookerId).getBody();
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwnerId(@RequestHeader(USER_ID) Long ownerId,
                                         @RequestParam(defaultValue = "ALL") String state,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getByOwnerId() в BookingController для пользователя с id {}, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", ownerId, from, size);
        Pageable pageable = PageRequest.of(from / size, size, DEFAULT_SORT);
        List<BookingDto> getByOwnerId = bookingService.getAllByOwnerId(ownerId, state, pageable);

        return ResponseEntity.ok().body(getByOwnerId).getBody();
    }
}