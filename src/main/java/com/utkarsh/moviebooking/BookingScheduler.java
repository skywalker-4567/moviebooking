package com.utkarsh.moviebooking;

import com.utkarsh.moviebooking.entity.Booking;
import com.utkarsh.moviebooking.enums.BookingStatus;
import com.utkarsh.moviebooking.repository.BookingRepository;
import com.utkarsh.moviebooking.repository.ShowSeatRepository;
import com.utkarsh.moviebooking.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingScheduler {

    private final BookingRepository bookingRepository;
    private final ShowSeatRepository showSeatRepository;
    private final EmailService emailService;

    /**
     * Runs every 5 minutes.
     * Cancels PENDING bookings older than 15 minutes
     * (i.e. user held seats but never completed payment).
     */
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void expireUnconfirmedBookings() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);

        List<Booking> staleBookings = bookingRepository
                .findByStatusAndBookedAtBefore(BookingStatus.PENDING, cutoff);

        if (staleBookings.isEmpty()) return;

        log.info("Expiring {} unconfirmed bookings", staleBookings.size());

        for (Booking booking : staleBookings) {
            // Release seats back to available
            booking.getShowSeats().forEach(ss -> ss.setBooked(false));
            showSeatRepository.saveAll(booking.getShowSeats());

            booking.setStatus(BookingStatus.CANCELLED);
            booking.setCancelledAt(LocalDateTime.now());
            bookingRepository.save(booking);
        }
    }

    /**
     * Runs once every day at 10:00 AM.
     * Sends reminder emails for shows happening the next day.
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void sendShowReminders() {
        LocalDateTime start = LocalDateTime.now().plusHours(23);
        LocalDateTime end   = LocalDateTime.now().plusHours(25);

        List<Booking> upcomingBookings = bookingRepository
                .findConfirmedBookingsWithShowBetween(start, end);

        log.info("Sending reminders for {} bookings", upcomingBookings.size());

        upcomingBookings.forEach(emailService::sendShowReminder);
    }
}
