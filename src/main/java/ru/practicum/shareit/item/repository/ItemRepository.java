package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findByOwnerId(int ownerId, Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))" +
            "AND i.available = TRUE")
    Page<Item> searchAvailableItems(String text, Pageable pageable);

    List<Item> findByRequestId(int requestId);
}