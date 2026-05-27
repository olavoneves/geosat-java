package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ImagemSatelitalRequest(
        @NotNull(message = "ID do talhão é obrigatório")
        Long idTalhao,

        @NotNull(message = "Data de captura é obrigatória")
        LocalDate dtCaptura,

        @NotBlank(message = "Fonte é obrigatória")
        @Pattern(regexp = "^(NASA|ESA)$", message = "Fonte deve ser NASA ou ESA")
        String dsFonte
) {}
