package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_PROPRIEDADE")
@Getter @Setter @NoArgsConstructor
public class Propriedade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_propriedade")
    @SequenceGenerator(name = "sq_propriedade", sequenceName = "sq_propriedade", allocationSize = 1)
    @Column(name = "ID_PROPRIEDADE")
    private Long idPropriedade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUTOR", nullable = false)
    private Produtor produtor;

    @Column(name = "NM_NOME", nullable = false, length = 100)
    private String nmNome;

    @Column(name = "NM_MUNICIPIO", nullable = false, length = 100)
    private String nmMunicipio;

    @Column(name = "SG_ESTADO", nullable = false, length = 2)
    private String sgEstado;

    @Column(name = "NR_AREA_HA", nullable = false, precision = 10, scale = 2)
    private BigDecimal nrAreaHa;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "flAtivo", column = @Column(name = "FL_ATIVA", length = 1)),
        @AttributeOverride(name = "dtCriacao", column = @Column(name = "DT_CRIACAO", updatable = false))
    })
    private Auditoria auditoria = new Auditoria();

    public String getFlAtiva() { return auditoria.getFlAtivo(); }
    public void setFlAtiva(String v) { auditoria.setFlAtivo(v); }
    public LocalDateTime getDtCriacao() { return auditoria.getDtCriacao(); }
    public void setDtCriacao(LocalDateTime v) { auditoria.setDtCriacao(v); }
}
