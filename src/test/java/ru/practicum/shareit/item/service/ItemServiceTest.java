package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.item.mapper.CommentMapper.commentToDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.itemToDto;

@WebMvcTest(ItemService.class)
@AutoConfigureMockMvc
class ItemServiceTest {

    ItemService itemService;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    User owner;

    User booker;

    Item item;

    ItemRequest itemRequest;

    Booking booking;

    Comment comment;

    @BeforeEach
    void init() {
        owner = User.builder()
                .id(1)
                .name("Вадим Фаустов")
                .email("vadimfaustov@gmail.com")
                .build();

        booker = User.builder()
                .id(2)
                .name("Тим Кук")
                .email("tcook@apple.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("Нужны наушники Apple AirPods Pro 2")
                .requester(booker)
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1)
                .name("Apple AirPods Pro 2")
                .description("Обновленные беспроводные наушники Apple")
                .owner(owner)
                .available(true)
                .request(itemRequest)
                .build();

        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now().minusDays(14))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        comment = Comment.builder()
                .id(1)
                .text("Надёжный, эффективный, качественный.")
                .item(item)
                .author(booker)
                .created(LocalDateTime.now())
                .build();

        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository);
    }


    @Test
    void createTest() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));

        ItemDto itemDto = itemService.create(itemToDto(item), owner.getId());

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
    }

    @Test
    void createRequestNullTest() {
        item.setRequest(null);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));

        ItemDto itemDto = itemService.create(itemToDto(item), owner.getId());

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void createNotFoundUserTest() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));

        Exception ex = assertThrows(NotFoundException.class, () -> itemService.create(itemToDto(item), owner.getId()));
        assertEquals(String.format("Пользователь с id = %s не найден.", owner.getId()), ex.getMessage());
    }

    @Test
    void createNotFoundItemRequestTest() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> itemService.create(itemToDto(item), owner.getId()));
        assertEquals(String.format("Запрос на вещь с id = %s не найден", item.getRequest().getId()), ex.getMessage());
    }

    @Test
    void updateTest() {
        Item updatedItem = Item.builder()
                .id(1)
                .name("AirPods Pro")
                .description("Просто волшебно. Как никогда.")
                .available(true)
                .request(itemRequest)
                .build();

        ItemDto updatedItemDto = ItemDto.builder()
                .id(updatedItem.getId())
                .name(updatedItem.getName())
                .description(updatedItem.getDescription())
                .available(updatedItem.getAvailable())
                .requestId(updatedItem.getRequest().getId())
                .build();

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(updatedItem);

        ItemDto result = itemService.update(updatedItemDto, owner.getId(), updatedItem.getId());

        assertNotNull(result);
        assertEquals(updatedItemDto.getId(), result.getId());
        assertEquals(updatedItemDto.getName(), result.getName());
        assertEquals(updatedItemDto.getDescription(), result.getDescription());
    }

    @Test
    void updateNotFoundItemTest() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> itemService.update(itemToDto(item),
                owner.getId(), item.getId()));
        assertEquals(String.format("Вещь с id = %s не найдена.", item.getId()), ex.getMessage());
    }

    @Test
    void updateNoOwnerTest() {
        User anotherUser = User.builder()
                .id(2)
                .name("Сергей Брин")
                .email("brin@google.com")
                .build();

        item.setOwner(anotherUser);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        Exception ex = assertThrows(NotFoundException.class, () -> itemService.update(itemToDto(item),
                owner.getId(), item.getId()));
        assertEquals("Редактировать информацию о вещи может только ее владелец.", ex.getMessage());
    }

    @Test
    void updateNullNameAndDescriptionAndAvailableTrue() {
        Item updatedItem = Item.builder()
                .id(1)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(itemRequest)
                .build();

        ItemDto updatedItemDto = ItemDto.builder()
                .id(updatedItem.getId())
                .name(null)
                .description(null)
                .available(null)
                .requestId(updatedItem.getRequest().getId())
                .build();

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(updatedItem);

        ItemDto result = itemService.update(updatedItemDto, owner.getId(), updatedItem.getId());

        assertNotNull(result);
        assertEquals(updatedItemDto.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    void getByIdTest() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        ItemDtoWithBooking result = itemService.getById(item.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    void getByIdNotFoundTest() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> itemService.getById(item.getId(), owner.getId()));
        assertEquals(String.format("Вещь с id = %s не найдена.", item.getId()), ex.getMessage());
    }

    @Test
    void getAllByOwnerTest() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(anyInt(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDtoWithBooking> result = itemService.getAllByOwner(owner.getId(), Pageable.unpaged());

        assertEquals(1, result.size());
    }

    @Test
    void deleteTest() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        itemService.delete(itemToDto(item).getId());
        List<Item> items = itemRepository.findAll();

        assertEquals(0, items.size());
    }

    @Test
    void searchTest() {
        when(itemRepository.searchAvailableItems(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> result = itemService.search("AirPods", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchEmptyTextTest() {
        when(itemRepository.searchAvailableItems(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        List<ItemDto> result = itemService.search("", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(0, result.size());
    }


    @Test
    void addCommentTest() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBookerIdAndItemIdAndStatusAndEndBefore(anyInt(), anyInt(), any(), any()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto result = itemService.createComment(booker.getId(), item.getId(), commentToDto(comment));

        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getCreated(), result.getCreated());
    }


    @Test
    void addCommentForItemNotFoundTest() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBookerIdAndItemIdAndStatusAndEndBefore(anyInt(), anyInt(), any(), any()))
                .thenReturn(List.of(booking));

        Exception ex = assertThrows(NotFoundException.class, () -> itemService.createComment(booker.getId(),
                item.getId(), commentToDto(comment)));
        assertEquals(String.format("Вещь с id = %s не найдена.", item.getId()), ex.getMessage());
    }

    @Test
    void addCommentIfUserNotBookerTest() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBookerIdAndItemIdAndStatusAndEndBefore(anyInt(), anyInt(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(commentRepository.save(any()))
                .thenReturn(comment);

        Exception ex = assertThrows(BadRequestException.class, () -> itemService.createComment(booker.getId(),
                item.getId(), commentToDto(comment)));
        assertEquals(String.format("Пользователь с id = %s не осуществлял бронирование " +
                "вещи с id = %s", booker.getId(), item.getId()), ex.getMessage());
    }
}