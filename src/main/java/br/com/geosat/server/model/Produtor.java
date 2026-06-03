package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_PRODUTOR")
@Getter @Setter @NoArgsConstructor
public class Produtor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_produtor")
    @SequenceGenerator(name = "sq_produtor", sequenceName = "sq_produtor", allocationSize = 1)
    @Column(name = "ID_PRODUTOR")
    private Long idProdutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private UsuarioJava usuario;

    @Column(name = "NM_NOME", nullable = false, length = 100)
    private String nmNome;

    @Column(name = "NR_CPF", nullable = false, unique = true, length = 11)
    private String nrCpf;

    @Column(name = "DS_EMAIL", nullable = false, unique = true, length = 150)
    private String dsEmail;

    @Column(name = "NR_TELEFONE", length = 15)
    private String nrTelefone;

    @Column(name = "DS_FCM_TOKEN", length = 255)
    private String dsFcmToken;

    @Embedded
    private Auditoria auditoria = new Auditoria();

    public String getFlAtivo() { return auditoria.getFlAtivo(); }
    public void setFlAtivo(String v) { auditoria.setFlAtivo(v); }
    public LocalDateTime getDtCriacao() { return auditoria.getDtCriacao(); }
    public void setDtCriacao(LocalDateTime v) { auditoria.setDtCriacao(v); }
}
