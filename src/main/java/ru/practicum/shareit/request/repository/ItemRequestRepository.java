package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    Page<ItemRequest> findAllByRequesterIdIsNot(int requesterId, Pageable page);

    Page<ItemRequest> findAllByRequesterId(int requesterId, Pageable page);
}