package br.com.geosat.server.dto.response;

import br.com.geosat.server.model.Propriedade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PropriedadeResponse(
        Long idPropriedade,
        Long idProdutor,
        String nmNome,
        String nmMunicipio,
        String sgEstado,
        BigDecimal nrAreaHa,
        String flAtiva,
        LocalDateTime dtCriacao
) {
    public static PropriedadeResponse from(Propriedade p) {
        return new PropriedadeResponse(p.getIdPropriedade(), p.getProdutor().getIdProdutor(),
                p.getNmNome(), p.getNmMunicipio(), p.getSgEstado(), p.getNrAreaHa(),
                p.getFlAtiva(), p.getDtCriacao());
    }
}
