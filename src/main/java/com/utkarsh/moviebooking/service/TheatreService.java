package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.dto.request.TheatreRequest;
import com.utkarsh.moviebooking.dto.response.TheatreResponse;
import com.utkarsh.moviebooking.entity.Theatre;
import com.utkarsh.moviebooking.entity.User;
import com.utkarsh.moviebooking.exception.ResourceNotFoundException;
import com.utkarsh.moviebooking.repository.TheatreRepository;
import com.utkarsh.moviebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepository;
    private final UserRepository userRepository;

    public TheatreResponse create(Long ownerId, TheatreRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Theatre theatre = Theatre.builder()
                .name(request.name())
                .city(request.city())
                .address(request.address())
                .owner(owner)
                .build();

        return mapToResponse(theatreRepository.save(theatre));
    }

    public Page<TheatreResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return theatreRepository.findAll(pageable).map(this::mapToResponse);
    }

    public TheatreResponse getById(Long id) {
        return mapToResponse(theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found")));
    }

    public List<TheatreResponse> getMyTheatres(Long ownerId) {
        return theatreRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToResponse).toList();
    }

    private TheatreResponse mapToResponse(Theatre t) {
        return new TheatreResponse(t.getId(), t.getName(), t.getCity(),
                t.getAddress(), t.getOwner().getName());
    }
}
