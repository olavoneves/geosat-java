package br.com.geosat.server.dto.response;

import br.com.geosat.server.model.Sensor;

import java.time.LocalDateTime;

public record SensorResponse(
        Long idSensor,
        Long idTalhao,
        String cdIdentificadorHw,
        String dsLocalizacao,
        String flAtivo,
        LocalDateTime dtInstalacao
) {
    public static SensorResponse from(Sensor s) {
        return new SensorResponse(s.getIdSensor(), s.getTalhao().getIdTalhao(),
                s.getCdIdentificadorHw(), s.getDsLocalizacao(),
                s.getFlAtivo(), s.getDtInstalacao());
    }
}
