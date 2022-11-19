package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("Apple MacBook Pro")
                .description("Новый MacBook Pro. Невероятная мощь с чипом M1 Pro или M1 Max.")
                .available(true)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Apple MacBook Pro");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Новый MacBook Pro. Невероятная мощь с чипом M1 Pro или M1 Max.");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
    }
}