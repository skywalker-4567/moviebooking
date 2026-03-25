package com.utkarsh.moviebooking.dto.request;

import com.utkarsh.moviebooking.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password,
        Role role
) {}
