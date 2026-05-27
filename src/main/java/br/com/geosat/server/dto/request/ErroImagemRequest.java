package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.*;

public record ErroImagemRequest(
        @NotBlank(message = "Descrição do erro é obrigatória")
        @Size(max = 500)
        String dsErro
) {}
