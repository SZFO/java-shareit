package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {
    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        User owner = User.builder()
                .id(1L)
                .name("Вадим Фаустов")
                .email("vadimfaustov@gmail.com")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("Тим Кук")
                .email("tcook@apple.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Apple MacBook Pro")
                .description("Новый MacBook Pro. Невероятная мощь с чипом M1 Pro или M1 Max.")
                .owner(owner)
                .available(true)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().plusDays(7))
                .end(LocalDateTime.now().plusDays(30))
                .booker(booker)
                .status(Status.WAITING)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.name").isEqualTo("Apple MacBook Pro");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}