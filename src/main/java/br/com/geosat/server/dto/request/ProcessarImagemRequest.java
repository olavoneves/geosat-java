package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProcessarImagemRequest(
        @NotNull(message = "NDVI é obrigatório")
        @DecimalMin(value = "-1.0", message = "NDVI mínimo: -1.0")
        @DecimalMax(value = "1.0", message = "NDVI máximo: 1.0")
        BigDecimal nrNdvi
) {}
