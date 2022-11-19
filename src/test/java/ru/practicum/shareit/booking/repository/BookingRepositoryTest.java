package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    User booker;

    User owner;

    Item item;

    Booking booking;

    Booking lastBooking;

    Booking nextBooking;

    @BeforeEach
    void init() {
        owner = userRepository.save(User.builder()
                .id(1)
                .name("Вадим Фаустов")
                .email("vadimfaustov@gmail.com")
                .build());

        booker = userRepository.save(User.builder()
                .id(2)
                .name("Тим Кук")
                .email("tcook@apple.com")
                .build());

        item = itemRepository.save(Item.builder()
                .id(1)
                .name("Apple MacBook Pro")
                .description("Новый MacBook Pro. Невероятная мощь с чипом M1 Pro или M1 Max.")
                .owner(owner)
                .available(true)
                .build());

        booking = bookingRepository.save(Booking.builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());

        lastBooking = bookingRepository.save(Booking.builder()
                .id(2)
                .start(LocalDateTime.now().minusDays(15))
                .end(LocalDateTime.now().minusDays(5))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build());

        nextBooking = bookingRepository.save(Booking.builder()
                .id(3)
                .start(LocalDateTime.now().plusDays(14))
                .end(LocalDateTime.now().plusDays(30))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build());
    }

    @Test
    void findAllByBookerIdOrderByStartDescTest() {
        List<Booking> actual = List.of(nextBooking, booking, lastBooking);
        Page<Booking> result = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.toList(), equalTo(actual));
    }

    @Test
    void findAllByStartBeforeAndEndAfterAndBookerIdOrderByStartDescTest() {
        Page<Booking> result = bookingRepository.findAllByStartBeforeAndEndAfterAndBookerIdOrderByStartDesc(
                LocalDateTime.now().plusDays(20), LocalDateTime.now().plusDays(20), booker.getId(), Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.toList().get(0), equalTo(nextBooking));
    }

    @Test
    void findAllByEndBeforeAndBookerIdOrderByStartDescTest() {
        Page<Booking> result = bookingRepository.findAllByEndBeforeAndBookerIdOrderByStartDesc(LocalDateTime.now(),
                booker.getId(), Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.toList().get(0), equalTo(lastBooking));
    }

    @Test
    void findAllByStartAfterAndBookerIdOrderByStartDescTest() {
        Page<Booking> result = bookingRepository.findAllByStartAfterAndBookerIdOrderByStartDesc(LocalDateTime.now(),
                booker.getId(), Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.toList().get(0), equalTo(nextBooking));
    }

    @Test
    void findAllByStatusAndBookerIdOrderByStartDescTest() {
        Page<Booking> result = bookingRepository.findAllByStatusAndBookerIdOrderByStartDesc(Status.WAITING,
                booker.getId(), Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.toList().get(0), equalTo(booking));
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDescTest() {
        List<Booking> actual = List.of(nextBooking, booking, lastBooking);
        Page<Booking> result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(),
                Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.toList(), equalTo(actual));
    }

    @Test
    void findAllByStartBeforeAndEndAfterAndItemOwnerIdOrderByStartDescTest() {
        Page<Booking> result = bookingRepository.findAllByStartBeforeAndEndAfterAndItemOwnerIdOrderByStartDesc(
                LocalDateTime.now().plusDays(20), LocalDateTime.now().plusDays(20), owner.getId(), Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.toList().get(0), equalTo(nextBooking));
    }

    @Test
    void findAllByEndBeforeAndItemOwnerIdOrderByStartDescTest() {
        Page<Booking> result = bookingRepository.findAllByEndBeforeAndItemOwnerIdOrderByStartDesc(LocalDateTime.now(),
                owner.getId(), Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.toList().get(0), equalTo(lastBooking));
    }

    @Test
    void findAllByStartAfterAndItemOwnerIdOrderByStartDescTest() {
        Page<Booking> result = bookingRepository.findAllByStartAfterAndItemOwnerIdOrderByStartDesc(LocalDateTime.now(),
                owner.getId(), Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.toList().get(0), equalTo(nextBooking));
    }

    @Test
    void findAllByStatusAndItemOwnerIdOrderByStartDescTest() {
        Page<Booking> result = bookingRepository.findAllByStatusAndItemOwnerIdOrderByStartDesc(Status.APPROVED,
                owner.getId(), Pageable.unpaged());

        assertThat(result, hasItems());
        assertThat(result.toList().get(0), equalTo(nextBooking));
    }

    @Test
    void findBookingsByItemIdAndEndIsBeforeOrderByEndDescTest() {
        List<Booking> bookings = bookingRepository.findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(),
                LocalDateTime.now());

        assertThat(bookings, hasItems());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(lastBooking));
    }

    @Test
    void findBookingsByItemIdAndStartIsAfterOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository.findBookingsByItemIdAndStartIsAfterOrderByStartDesc(item.getId(),
                LocalDateTime.now());

        assertThat(bookings, hasItems());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0), equalTo(nextBooking));
    }
}