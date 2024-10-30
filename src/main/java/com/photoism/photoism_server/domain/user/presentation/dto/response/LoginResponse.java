package com.photoism.photoism_server.domain.user.presentation.dto.response;

import com.photoism.photoism_server.common.dto.TokenResponse;

public record LoginResponse(
        TokenResponse tokens

) {
}
