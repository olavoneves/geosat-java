package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_CONFIGURACAO")
@Getter @Setter @NoArgsConstructor
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_configuracao")
    @SequenceGenerator(name = "sq_configuracao", sequenceName = "sq_configuracao", allocationSize = 1)
    @Column(name = "ID_CONFIG")
    private Long idConfig;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TALHAO", nullable = false, unique = true)
    private Talhao talhao;

    @Column(name = "NR_THRESHOLD_UMID_MIN", precision = 5, scale = 2)
    private BigDecimal nrThresholdUmidMin = new BigDecimal("30.00");

    @Column(name = "NR_THRESHOLD_NDVI_MIN", precision = 4, scale = 3)
    private BigDecimal nrThresholdNdviMin = new BigDecimal("0.300");

    @Column(name = "NR_JANELA_FUSAO_HORAS")
    private Integer nrJanelaFusaoHoras = 48;

    @Column(name = "DT_ATUALIZACAO")
    private LocalDateTime dtAtualizacao;

    @PrePersist
    @PreUpdate
    void preUpdate() {
        dtAtualizacao = LocalDateTime.now();
    }
}
