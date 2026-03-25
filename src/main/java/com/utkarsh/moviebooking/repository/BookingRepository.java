package com.utkarsh.moviebooking.repository;

import com.utkarsh.moviebooking.entity.Booking;
import com.utkarsh.moviebooking.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // BookingRepository
    Page<Booking> findByUserId(Long userId, Pageable pageable);
    List<Booking> findByStatusAndBookedAtBefore(BookingStatus status, LocalDateTime time);

    @Query("""
    SELECT b FROM Booking b
    JOIN FETCH b.show s
    JOIN FETCH b.user
    JOIN FETCH b.showSeats ss
    JOIN FETCH ss.seat
    WHERE b.status = 'CONFIRMED'
    AND s.startTime BETWEEN :start AND :end
""")
    List<Booking> findConfirmedBookingsWithShowBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
