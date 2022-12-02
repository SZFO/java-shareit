package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    User requester1;

    User requester2;

    ItemRequest itemRequest1;

    ItemRequest itemRequest2;

    ItemRequest itemRequest3;

    @BeforeEach
    void init() {
        requester1 = userRepository.save(User.builder()
                .id(1L)
                .name("Ларри Эллисон")
                .email("ellison@oracle.com")
                .build());

        requester2 = userRepository.save(User.builder()
                .id(2L)
                .name("Тим Кук")
                .email("tcook@apple.com")
                .build());

        itemRequest1 = itemRequestRepository.save(ItemRequest.builder()
                .id(1L)
                .description("Нужны наушники Apple AirPods Pro 2.")
                .requester(requester1)
                .created(LocalDateTime.now())
                .build());

        itemRequest2 = itemRequestRepository.save(ItemRequest.builder()
                .id(2L)
                .description("Нужен Macbook Pro 2022.")
                .requester(requester1)
                .created(LocalDateTime.now())
                .build());

        itemRequest3 = itemRequestRepository.save(ItemRequest.builder()
                .id(3L)
                .description("Нужен мощный перфоратор. В компании планируется ремонт.")
                .requester(requester2)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void findAllByRequesterIdIsNotTest() {
        Page<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdIsNot(requester1.getId(),
                Pageable.unpaged());

        assertThat(requests, hasItems());
        assertThat(requests.toList().size(), equalTo(1));
        assertThat(requests.toList().get(0), equalTo(itemRequest3));
    }

    @Test
    void findByRequesterIdTest() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(requester1.getId());

        assertThat(requests, hasItems());
        assertThat(requests.size(), equalTo(2));
        assertThat(requests, equalTo(List.of(itemRequest1, itemRequest2)));
    }
}