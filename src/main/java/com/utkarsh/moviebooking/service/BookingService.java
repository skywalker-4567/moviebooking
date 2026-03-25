package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.dto.request.BookingConfirmRequest;
import com.utkarsh.moviebooking.dto.request.SeatHoldRequest;
import com.utkarsh.moviebooking.dto.response.BookingResponse;
import com.utkarsh.moviebooking.entity.Booking;
import com.utkarsh.moviebooking.entity.Show;
import com.utkarsh.moviebooking.entity.ShowSeat;
import com.utkarsh.moviebooking.entity.User;
import com.utkarsh.moviebooking.enums.BookingStatus;
import com.utkarsh.moviebooking.exception.ResourceNotFoundException;
import com.utkarsh.moviebooking.exception.SeatNotAvailableException;
import com.utkarsh.moviebooking.repository.BookingRepository;
import com.utkarsh.moviebooking.repository.ShowRepository;
import com.utkarsh.moviebooking.repository.ShowSeatRepository;
import com.utkarsh.moviebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ShowSeatRepository showSeatRepository;
    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final SeatHoldService seatHoldService;
    private final UserRepository userRepository;

    private final EmailService emailService;
    private final PaymentService paymentService;

    // Step 1: Customer selects seats — hold them in Redis
    public void holdSeats(Long userId, SeatHoldRequest request) {
        for (Long showSeatId : request.showSeatIds()) {
            boolean held = seatHoldService.holdSeat(
                    request.showId(), showSeatId, userId
            );
            if (!held) {
                // Another user holds this seat — release any we already held
                // and reject the whole request
                request.showSeatIds().forEach(id ->
                        seatHoldService.releaseHold(request.showId(), id)
                );
                throw new SeatNotAvailableException(
                        "Seat " + showSeatId + " is currently held by another user."
                );
            }
        }
    }

    // Step 2: Customer pays — confirm the booking
    @Transactional
    public BookingResponse confirmBooking(Long userId, BookingConfirmRequest request) {

        // Verify Redis hold still exists and belongs to this user
        for (Long showSeatId : request.showSeatIds()) {
            if (!seatHoldService.isHeldByUser(request.showId(), showSeatId, userId)) {
                throw new SeatNotAvailableException(
                        "Hold expired or invalid for seat " + showSeatId
                );
            }
        }

        // Fetch ShowSeats with OPTIMISTIC lock
        // If two requests somehow pass the Redis check and reach here
        // simultaneously, this is the last line of defence
        List<ShowSeat> showSeats = showSeatRepository
                .findByIdsWithLock(request.showSeatIds());

        // Verify none are already booked in the DB
        for (ShowSeat ss : showSeats) {
            if (ss.isBooked()) {
                throw new SeatNotAvailableException(
                        "Seat " + ss.getSeat().getSeatNumber() + " is already booked."
                );
            }
        }

        // Mark seats as booked
        showSeats.forEach(ss -> ss.setBooked(true));
        showSeatRepository.saveAll(showSeats);
        // If two transactions reach saveAll at the same time with the same
        // @Version value, the second one throws OptimisticLockException here

        Show show = showRepository.findById(request.showId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BigDecimal total = showSeats.stream()
                .map(ss -> ss.getSeat().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Booking booking = Booking.builder()
                .user(user)
                .show(show)
                .showSeats(showSeats)
                .status(BookingStatus.PENDING)
                .totalAmount(total)
                .bookedAt(LocalDateTime.now())
                .build();

        Booking saved = bookingRepository.save(booking);

        // Record payment and confirm
        paymentService.recordPayment(saved);
        saved.setStatus(BookingStatus.CONFIRMED);
        emailService.sendBookingConfirmation(saved);
        bookingRepository.save(saved);

        // Release Redis holds — seats are now locked in DB
        request.showSeatIds().forEach(id ->
                seatHoldService.releaseHold(request.showId(), id)
        );

        return mapToResponse(saved);
    }

    private BookingResponse mapToResponse(Booking booking) {
        List<String> seatNumbers = booking.getShowSeats().stream()
                .map(ss -> ss.getSeat().getSeatNumber())
                .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getShow().getMovie().getTitle(),
                booking.getShow().getStartTime(),
                seatNumbers,
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getBookedAt()
        );
    }
    // In BookingService
    public Page<BookingResponse> getBookingsByUser(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }
}
