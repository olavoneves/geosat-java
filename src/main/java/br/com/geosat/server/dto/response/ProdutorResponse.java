package br.com.geosat.server.dto.response;

import br.com.geosat.server.model.Produtor;

import java.time.LocalDateTime;

public record ProdutorResponse(
        Long idProdutor,
        Long idUsuario,
        String nmNome,
        String nrCpf,
        String dsEmail,
        String nrTelefone,
        String flAtivo,
        LocalDateTime dtCriacao
) {
    public static ProdutorResponse from(Produtor p) {
        return new ProdutorResponse(p.getIdProdutor(), p.getUsuario().getIdUsuario(),
                p.getNmNome(), p.getNrCpf(), p.getDsEmail(), p.getNrTelefone(),
                p.getFlAtivo(), p.getDtCriacao());
    }
}
