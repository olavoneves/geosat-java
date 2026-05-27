package br.com.geosat.server.dto.response;

import br.com.geosat.server.model.UsuarioJava;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long idUsuario,
        String nmNome,
        String dsEmail,
        String dsRole,
        String flAtivo,
        LocalDateTime dtCriacao
) {
    public static UsuarioResponse from(UsuarioJava u) {
        return new UsuarioResponse(u.getIdUsuario(), u.getNmNome(), u.getDsEmail(),
                u.getDsRole(), u.getFlAtivo(), u.getDtCriacao());
    }
}
