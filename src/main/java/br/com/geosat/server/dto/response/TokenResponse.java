package br.com.geosat.server.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String role
) {}
