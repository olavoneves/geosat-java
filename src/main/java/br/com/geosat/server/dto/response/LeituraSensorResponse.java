package br.com.geosat.server.dto.response;

import br.com.geosat.server.model.LeituraSensor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraSensorResponse(
        Long idLeitura,
        Long idSensor,
        LocalDateTime dtLeitura,
        BigDecimal nrTempAr,
        BigDecimal nrUmidadeSolo,
        BigDecimal nrLuminosidade,
        String flTransmitida,
        LocalDateTime dtRecebida
) {
    public static LeituraSensorResponse from(LeituraSensor l) {
        return new LeituraSensorResponse(l.getIdLeitura(), l.getSensor().getIdSensor(),
                l.getDtLeitura(), l.getNrTempAr(), l.getNrUmidadeSolo(),
                l.getNrLuminosidade(), l.getFlTransmitida(), l.getDtRecebida());
    }
}
