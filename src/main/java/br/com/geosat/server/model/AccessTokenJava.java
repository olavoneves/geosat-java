package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_ACCESS_TOKEN_JAVA")
@Getter @Setter @NoArgsConstructor
public class AccessTokenJava {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_access_java")
    @SequenceGenerator(name = "sq_access_java", sequenceName = "sq_access_java", allocationSize = 1)
    @Column(name = "ID_ACCESS")
    private Long idAccess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private UsuarioJava usuario;

    @Column(name = "DS_TOKEN", nullable = false, unique = true, length = 255)
    private String dsToken;

    @Column(name = "DT_EXPIRACAO", nullable = false)
    private LocalDateTime dtExpiracao;

    @Column(name = "FL_REVOGADO", length = 1)
    private String flRevogado = "N";

    @Column(name = "DT_CRIACAO", updatable = false)
    private LocalDateTime dtCriacao;

    @PrePersist
    void prePersist() {
        if (dtCriacao == null) dtCriacao = LocalDateTime.now();
        if (flRevogado == null) flRevogado = "N";
    }
}
