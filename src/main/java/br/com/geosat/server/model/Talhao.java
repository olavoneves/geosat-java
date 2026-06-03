package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_TALHAO")
@Getter @Setter @NoArgsConstructor
public class Talhao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_talhao")
    @SequenceGenerator(name = "sq_talhao", sequenceName = "sq_talhao", allocationSize = 1)
    @Column(name = "ID_TALHAO")
    private Long idTalhao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROPRIEDADE", nullable = false)
    private Propriedade propriedade;

    @Column(name = "NM_NOME", nullable = false, length = 100)
    private String nmNome;

    @Column(name = "DS_CULTURA", nullable = false, length = 50)
    private String dsCultura;

    @Column(name = "NR_AREA_HA", nullable = false, precision = 8, scale = 2)
    private BigDecimal nrAreaHa;

    @Embedded
    private Auditoria auditoria = new Auditoria();

    public String getFlAtivo() { return auditoria.getFlAtivo(); }
    public void setFlAtivo(String v) { auditoria.setFlAtivo(v); }
    public LocalDateTime getDtCriacao() { return auditoria.getDtCriacao(); }
    public void setDtCriacao(LocalDateTime v) { auditoria.setDtCriacao(v); }
}
