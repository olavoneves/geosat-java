package br.com.geosat.server.dto.response;

import br.com.geosat.server.model.Alerta;

import java.time.LocalDateTime;

public record AlertaResponse(
        Long idAlerta,
        Long idTalhao,
        String tpTipo,
        String tpNivel,
        String tpOrigem,
        String dsDescricao,
        String stStatus,
        LocalDateTime dtGerado,
        LocalDateTime dtVisualizado,
        LocalDateTime dtResolvido
) {
    public static AlertaResponse from(Alerta a) {
        return new AlertaResponse(a.getIdAlerta(), a.getTalhao().getIdTalhao(),
                a.getTpTipo(), a.getTpNivel(), a.getTpOrigem(), a.getDsDescricao(),
                a.getStStatus(), a.getDtGerado(), a.getDtVisualizado(), a.getDtResolvido());
    }
}
