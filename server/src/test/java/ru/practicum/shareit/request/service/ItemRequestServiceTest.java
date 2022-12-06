package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.mapper.UserMapper.userToDto;

@WebMvcTest(ItemRequestService.class)
@AutoConfigureMockMvc
class ItemRequestServiceTest {
    ItemRequestService itemRequestService;

    @MockBean
    UserService userService;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @MockBean
    ItemRepository itemRepository;

    Item item;

    User owner;

    User requester;

    ItemRequest itemRequest;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        owner = User.builder()
                .id(1L)
                .name("Тим Кук")
                .email("tcook@apple.com")
                .build();

        requester = User.builder()
                .id(2L)
                .name("Ларри Эллисон")
                .email("ellison@oracle.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Нужны наушники Apple AirPods Pro 2.")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("Apple AirPods Pro 2.")
                .description("Обновленные беспроводные наушники Apple, которые представили 7 сентября 2022 года")
                .owner(owner)
                .available(true)
                .request(itemRequest)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();

        itemRequestService = new ItemRequestServiceImpl(userService, itemRequestRepository, itemRepository);
    }

    @Test
    void saveTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userToDto(requester));
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.save(requester.getId(), itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
    }

    @Test
    void getByIdTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userToDto(requester));
        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        ItemRequestDtoOut result = itemRequestService.getById(requester.getId(), itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
    }

    @Test
    void getByIdNotFoundTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userToDto(requester));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(requester.getId(),
                itemRequest.getId()));
    }

    @Test
    void getAllTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userToDto(requester));
        when(itemRequestRepository.findAllByRequesterId(anyLong()))
                .thenReturn((List.of(itemRequest)));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(List.of(item));


        List<ItemRequestDtoOut> result = itemRequestService.getAll(requester.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllFromOtherUserTest() {
        when(userService.getById(anyLong()))
                .thenReturn(userToDto(requester));
        when(itemRequestRepository.findAllByRequesterIdIsNot(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        List<ItemRequestDtoOut> result = itemRequestService.getAllFromOtherUser(requester.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}