package com.photoism.photoism_server.common.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}

