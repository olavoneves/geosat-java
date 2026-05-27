package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraSensorRequest(
        @NotNull(message = "ID do sensor é obrigatório")
        Long idSensor,

        @NotNull(message = "Data da leitura é obrigatória")
        LocalDateTime dtLeitura,

        @NotNull(message = "Temperatura do ar é obrigatória")
        @DecimalMin(value = "-50.0", message = "Temperatura mínima: -50°C")
        @DecimalMax(value = "70.0", message = "Temperatura máxima: 70°C")
        BigDecimal nrTempAr,

        @NotNull(message = "Umidade do solo é obrigatória")
        @DecimalMin(value = "0.0", message = "Umidade mínima: 0%")
        @DecimalMax(value = "100.0", message = "Umidade máxima: 100%")
        BigDecimal nrUmidadeSolo,

        @DecimalMin(value = "0.0", message = "Luminosidade não pode ser negativa")
        BigDecimal nrLuminosidade
) {}
