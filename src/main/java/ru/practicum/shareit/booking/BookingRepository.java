package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithBookerAndItem(@Param("bookingId") Long bookingId);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByItemIdInOrderByStartDesc(List<Long> itemIds);

    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemIdInAndEndBeforeOrderByStartDesc(List<Long> itemIds, LocalDateTime end);

    List<Booking> findByItemIdInAndStartAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime start);

    List<Booking> findByItemIdInAndStatusOrderByStartDesc(List<Long> itemIds, Status status);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerIdWithItem(@Param("bookerId") Long bookerId);

    List<Booking> findByItemOwnerId(Long userId);
}
