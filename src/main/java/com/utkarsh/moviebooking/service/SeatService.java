package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.dto.request.SeatRequest;
import com.utkarsh.moviebooking.dto.response.SeatResponse;
import com.utkarsh.moviebooking.entity.Screen;
import com.utkarsh.moviebooking.entity.Seat;
import com.utkarsh.moviebooking.exception.ResourceNotFoundException;
import com.utkarsh.moviebooking.repository.ScreenRepository;
import com.utkarsh.moviebooking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;

    public SeatResponse create(SeatRequest request) {
        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

        Seat seat = Seat.builder()
                .seatNumber(request.seatNumber())
                .seatType(request.seatType())
                .price(request.price())
                .screen(screen)
                .build();

        return mapToResponse(seatRepository.save(seat));
    }

    public List<SeatResponse> getByScreen(Long screenId) {
        return seatRepository.findByScreenId(screenId).stream()
                .map(this::mapToResponse).toList();
    }

    private SeatResponse mapToResponse(Seat s) {
        return new SeatResponse(s.getId(), s.getSeatNumber(),
                s.getSeatType(), s.getPrice(), s.getScreen().getId());
    }
}
