package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(int bookerId);

    List<Booking> findAllByStartBeforeAndEndAfterAndBooker_IdOrderByStartDesc(LocalDateTime start,
                                                                              LocalDateTime end,
                                                                              int bookerId);

    List<Booking> findAllByEndBeforeAndBooker_IdOrderByStartDesc(LocalDateTime end, int bookerId);

    List<Booking> findAllByStartAfterAndBooker_IdOrderByStartDesc(LocalDateTime start, int bookerId);

    List<Booking> findAllByStatusAndBooker_IdOrderByStartDesc(Status bookingStatus, int bookerId);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(int ownerId);

    List<Booking> findAllByStartBeforeAndEndAfterAndItem_Owner_IdOrderByStartDesc(LocalDateTime start,
                                                                                  LocalDateTime end,
                                                                                  int ownerId);

    List<Booking> findAllByEndBeforeAndItem_Owner_IdOrderByStartDesc(LocalDateTime end, int ownerId);

    List<Booking> findAllByStartAfterAndItem_Owner_IdOrderByStartDesc(LocalDateTime start, int ownerId);

    List<Booking> findAllByStatusAndItem_Owner_IdOrderByStartDesc(Status status, int ownerId);

    Optional<Booking> findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(int id, LocalDateTime time);

    Optional<Booking> findBookingsByItemIdAndStartIsAfterOrderByStartDesc(int id, LocalDateTime time);

    List<Booking> findBookingsByBooker_IdAndItem_IdAndStatusAndEndBefore(int userId, int itemId,
                                                                         Status bookingStatus,
                                                                         LocalDateTime localDateTime);
}