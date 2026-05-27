package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ConfiguracaoRequest(
        @NotNull(message = "Threshold de umidade mínima é obrigatório")
        @DecimalMin(value = "0.0", message = "Umidade mínima: 0%")
        @DecimalMax(value = "100.0", message = "Umidade máxima: 100%")
        BigDecimal nrThresholdUmidMin,

        @NotNull(message = "Threshold de NDVI mínimo é obrigatório")
        @DecimalMin(value = "-1.0", message = "NDVI mínimo: -1.0")
        @DecimalMax(value = "1.0", message = "NDVI máximo: 1.0")
        BigDecimal nrThresholdNdviMin,

        @NotNull(message = "Janela de fusão em horas é obrigatória")
        @Min(value = 1, message = "Janela mínima: 1 hora")
        @Max(value = 720, message = "Janela máxima: 720 horas")
        Integer nrJanelaFusaoHoras
) {}
