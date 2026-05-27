package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_USUARIO_JAVA")
@Getter @Setter @NoArgsConstructor
public class UsuarioJava {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_usuario_java")
    @SequenceGenerator(name = "sq_usuario_java", sequenceName = "sq_usuario_java", allocationSize = 1)
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "NM_NOME", nullable = false, length = 100)
    private String nmNome;

    @Column(name = "DS_EMAIL", nullable = false, unique = true, length = 150)
    private String dsEmail;

    @Column(name = "DS_SENHA_HASH", nullable = false, length = 255)
    private String dsSenhaHash;

    @Column(name = "DS_ROLE", length = 10)
    private String dsRole = "USER";

    @Column(name = "FL_ATIVO", length = 1)
    private String flAtivo = "S";

    @Column(name = "DT_CRIACAO", updatable = false)
    private LocalDateTime dtCriacao;

    @PrePersist
    void prePersist() {
        if (dtCriacao == null) dtCriacao = LocalDateTime.now();
        if (flAtivo == null) flAtivo = "S";
        if (dsRole == null) dsRole = "USER";
    }
}
