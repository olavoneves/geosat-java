package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token é obrigatório")
        String refreshToken
) {}
