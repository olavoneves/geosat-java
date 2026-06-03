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

    @Embedded
    private Auditoria auditoria = new Auditoria();

    @PrePersist
    void prePersist() {
        if (dsRole == null) dsRole = "USER";
    }

    public String getFlAtivo() { return auditoria.getFlAtivo(); }
    public void setFlAtivo(String v) { auditoria.setFlAtivo(v); }
    public LocalDateTime getDtCriacao() { return auditoria.getDtCriacao(); }
    public void setDtCriacao(LocalDateTime v) { auditoria.setDtCriacao(v); }
}
