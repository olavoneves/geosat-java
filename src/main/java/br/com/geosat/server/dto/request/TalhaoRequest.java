package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TalhaoRequest(
        @NotNull(message = "ID da propriedade é obrigatório")
        Long idPropriedade,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        String nmNome,

        @NotBlank(message = "Cultura é obrigatória")
        @Size(max = 50)
        String dsCultura,

        @NotNull(message = "Área em hectares é obrigatória")
        @DecimalMin(value = "0.01", message = "Área deve ser maior que zero")
        BigDecimal nrAreaHa
) {}
