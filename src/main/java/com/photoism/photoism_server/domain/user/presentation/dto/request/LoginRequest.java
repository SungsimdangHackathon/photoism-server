package com.photoism.photoism_server.domain.user.presentation.dto.request;

public record LoginRequest(
        String email,
        String password

) {
}
