package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user;

    Item item;

    ItemRequest itemRequest;

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .id(1)
                .name("Вадим Фаустов")
                .email("vadimfaustov@gmail.com")
                .build());

        itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .id(1)
                .description("Нужны наушники Apple AirPods Pro 2")
                .requester(user)
                .created(LocalDateTime.now())
                .build());

        item = itemRepository.save(Item.builder()
                .id(1)
                .name("Apple AirPods Pro 2")
                .description("Обновленные беспроводные наушники Apple")
                .owner(user)
                .available(true)
                .request(itemRequest)
                .build());
    }

    @Test
    void searchAvailableItemsTest() {
        Page<Item> result = itemRepository.searchAvailableItems("Apple AirPods Pro 2", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(item, result.stream().findFirst().orElse(null));
    }

    @Test
    void findByOwnerIdTest() {
        Page<Item> result = itemRepository.findByOwnerId(user.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findByRequestIdTest() {
        List<Item> result = itemRepository.findByRequestId(itemRequest.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}