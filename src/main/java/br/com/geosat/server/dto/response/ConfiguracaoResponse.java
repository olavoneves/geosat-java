package br.com.geosat.server.dto.response;

import br.com.geosat.server.model.Configuracao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConfiguracaoResponse(
        Long idConfig,
        Long idTalhao,
        BigDecimal nrThresholdUmidMin,
        BigDecimal nrThresholdNdviMin,
        Integer nrJanelaFusaoHoras,
        LocalDateTime dtAtualizacao
) {
    public static ConfiguracaoResponse from(Configuracao c) {
        return new ConfiguracaoResponse(c.getIdConfig(), c.getTalhao().getIdTalhao(),
                c.getNrThresholdUmidMin(), c.getNrThresholdNdviMin(),
                c.getNrJanelaFusaoHoras(), c.getDtAtualizacao());
    }
}
