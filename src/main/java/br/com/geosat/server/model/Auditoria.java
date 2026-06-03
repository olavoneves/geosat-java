package br.com.geosat.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
public class Auditoria {

    @Column(name = "FL_ATIVO", length = 1)
    private String flAtivo = "S";

    @Column(name = "DT_CRIACAO", updatable = false)
    private LocalDateTime dtCriacao;

    @PrePersist
    public void prePersist() {
        if (dtCriacao == null) dtCriacao = LocalDateTime.now();
        if (flAtivo == null) flAtivo = "S";
    }
}
