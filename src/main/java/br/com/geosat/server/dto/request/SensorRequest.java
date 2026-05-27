package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.*;

public record SensorRequest(
        @NotNull(message = "ID do talhão é obrigatório")
        Long idTalhao,

        @NotBlank(message = "Identificador de hardware é obrigatório")
        @Size(max = 50)
        String cdIdentificadorHw,

        @Size(max = 200)
        String dsLocalizacao
) {}
