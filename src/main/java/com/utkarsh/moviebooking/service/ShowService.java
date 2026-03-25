package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.dto.request.ShowRequest;
import com.utkarsh.moviebooking.dto.response.ShowResponse;
import com.utkarsh.moviebooking.entity.*;
import com.utkarsh.moviebooking.exception.ResourceNotFoundException;
import com.utkarsh.moviebooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final ShowSeatRepository showSeatRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public ShowResponse create(ShowRequest request) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .basePrice(request.basePrice())
                .build();

        Show saved = showRepository.save(show);

        // Auto-generate ShowSeat records for every seat in this screen
        List<Seat> seats = seatRepository.findByScreenId(screen.getId());
        List<ShowSeat> showSeats = seats.stream()
                .map(seat -> ShowSeat.builder()
                        .show(saved)
                        .seat(seat)
                        .isBooked(false)
                        .build())
                .toList();
        showSeatRepository.saveAll(showSeats);

        return mapToResponse(saved);
    }

    public Page<ShowResponse> getShowsByMovie(Long movieId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
        return showRepository.findByMovieId(movieId, pageable).map(this::mapToResponse);
    }

    private ShowResponse mapToResponse(Show s) {
        return new ShowResponse(s.getId(), s.getMovie().getTitle(),
                s.getScreen().getName(), s.getScreen().getTheatre().getName(),
                s.getStartTime(), s.getEndTime(), s.getBasePrice());
    }
}