package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findAllByStartBeforeAndEndAfterAndBookerIdOrderByStartDesc(LocalDateTime start, LocalDateTime end,
                                                                             Long bookerId, Pageable pageable);

    Page<Booking> findAllByEndBeforeAndBookerIdOrderByStartDesc(LocalDateTime end, Long bookerId, Pageable pageable);

    Page<Booking> findAllByStartAfterAndBookerIdOrderByStartDesc(LocalDateTime start, Long bookerId, Pageable pageable);

    Page<Booking> findAllByStatusAndBookerIdOrderByStartDesc(Status status, Long bookerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findAllByStartBeforeAndEndAfterAndItemOwnerIdOrderByStartDesc(LocalDateTime start,
                                                                                LocalDateTime end,
                                                                                Long ownerId, Pageable pageable);

    Page<Booking> findAllByEndBeforeAndItemOwnerIdOrderByStartDesc(LocalDateTime end, Long ownerId,
                                                                   Pageable pageable);

    Page<Booking> findAllByStartAfterAndItemOwnerIdOrderByStartDesc(LocalDateTime start, Long ownerId,
                                                                    Pageable pageable);

    Page<Booking> findAllByStatusAndItemOwnerIdOrderByStartDesc(Status status, Long ownerId, Pageable pageable);

    List<Booking> findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(Long id, LocalDateTime time);

    List<Booking> findBookingsByItemIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId,
                                                                          Status status, LocalDateTime end);
}