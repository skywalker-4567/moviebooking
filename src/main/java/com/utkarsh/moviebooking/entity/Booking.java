package com.utkarsh.moviebooking.entity;

import com.utkarsh.moviebooking.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    private Show show;

    @ManyToMany
    @JoinTable(name = "booking_show_seats",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "show_seat_id"))
    private List<ShowSeat> showSeats = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    // Add to Booking entity
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;
    private BigDecimal totalAmount;
    private LocalDateTime bookedAt;
    private LocalDateTime cancelledAt;
}
