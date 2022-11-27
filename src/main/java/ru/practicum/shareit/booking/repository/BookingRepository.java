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
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findAllByBookerIdOrderByStartDesc(int bookerId, Pageable pageable);

    Page<Booking> findAllByStartBeforeAndEndAfterAndBookerIdOrderByStartDesc(LocalDateTime start, LocalDateTime end,
                                                                             int bookerId, Pageable pageable);

    Page<Booking> findAllByEndBeforeAndBookerIdOrderByStartDesc(LocalDateTime end, int bookerId, Pageable pageable);

    Page<Booking> findAllByStartAfterAndBookerIdOrderByStartDesc(LocalDateTime start, int bookerId, Pageable pageable);

    Page<Booking> findAllByStatusAndBookerIdOrderByStartDesc(Status status, int bookerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(int ownerId, Pageable pageable);

    Page<Booking> findAllByStartBeforeAndEndAfterAndItemOwnerIdOrderByStartDesc(LocalDateTime start,
                                                                                LocalDateTime end,
                                                                                int ownerId, Pageable pageable);

    Page<Booking> findAllByEndBeforeAndItemOwnerIdOrderByStartDesc(LocalDateTime end, int ownerId,
                                                                   Pageable pageable);

    Page<Booking> findAllByStartAfterAndItemOwnerIdOrderByStartDesc(LocalDateTime start, int ownerId,
                                                                    Pageable pageable);

    Page<Booking> findAllByStatusAndItemOwnerIdOrderByStartDesc(Status status, int ownerId, Pageable pageable);

    List<Booking> findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(int id, LocalDateTime time);

    List<Booking> findBookingsByItemIdAndStartIsAfterOrderByStartDesc(int id, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndItemIdAndStatusAndEndBefore(int userId, int itemId,
                                                                       Status bookingStatus,
                                                                       LocalDateTime localDateTime);
}