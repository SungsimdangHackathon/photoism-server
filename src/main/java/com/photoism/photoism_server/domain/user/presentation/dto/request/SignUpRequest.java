package com.photoism.photoism_server.domain.user.presentation.dto.request;

public record SignUpRequest(
        String username,

        String password,

        String email
) {
}
