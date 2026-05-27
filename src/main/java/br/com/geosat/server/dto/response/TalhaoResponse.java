package br.com.geosat.server.dto.response;

import br.com.geosat.server.model.Talhao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TalhaoResponse(
        Long idTalhao,
        Long idPropriedade,
        String nmNome,
        String dsCultura,
        BigDecimal nrAreaHa,
        String flAtivo,
        LocalDateTime dtCriacao
) {
    public static TalhaoResponse from(Talhao t) {
        return new TalhaoResponse(t.getIdTalhao(), t.getPropriedade().getIdPropriedade(),
                t.getNmNome(), t.getDsCultura(), t.getNrAreaHa(),
                t.getFlAtivo(), t.getDtCriacao());
    }
}
