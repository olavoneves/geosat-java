package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PropriedadeRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        String nmNome,

        @NotBlank(message = "Município é obrigatório")
        @Size(max = 100)
        String nmMunicipio,

        @NotBlank(message = "Estado é obrigatório")
        @Pattern(regexp = "^[A-Z]{2}$", message = "Estado deve ser a UF com 2 letras maiúsculas (ex: SP)")
        String sgEstado,

        @NotNull(message = "Área em hectares é obrigatória")
        @DecimalMin(value = "0.01", message = "Área deve ser maior que zero")
        BigDecimal nrAreaHa
) {}
