package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.dto.request.ScreenRequest;
import com.utkarsh.moviebooking.dto.response.ScreenResponse;
import com.utkarsh.moviebooking.entity.Screen;
import com.utkarsh.moviebooking.entity.Theatre;
import com.utkarsh.moviebooking.exception.ResourceNotFoundException;
import com.utkarsh.moviebooking.repository.ScreenRepository;
import com.utkarsh.moviebooking.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;

    public ScreenResponse create(ScreenRequest request) {
        Theatre theatre = theatreRepository.findById(request.theatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found"));

        Screen screen = Screen.builder()
                .name(request.name())
                .totalSeats(request.totalSeats())
                .theatre(theatre)
                .build();

        return mapToResponse(screenRepository.save(screen));
    }

    public List<ScreenResponse> getByTheatre(Long theatreId) {
        return screenRepository.findByTheatreId(theatreId).stream()
                .map(this::mapToResponse).toList();
    }

    private ScreenResponse mapToResponse(Screen s) {
        return new ScreenResponse(s.getId(), s.getName(), s.getTotalSeats(),
                s.getTheatre().getId(), s.getTheatre().getName());
    }
}
