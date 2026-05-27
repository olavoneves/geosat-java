package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_ALERTA")
@Getter @Setter @NoArgsConstructor
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_alerta")
    @SequenceGenerator(name = "sq_alerta", sequenceName = "sq_alerta", allocationSize = 1)
    @Column(name = "ID_ALERTA")
    private Long idAlerta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TALHAO", nullable = false)
    private Talhao talhao;

    @Column(name = "TP_TIPO", nullable = false, length = 10)
    private String tpTipo;

    @Column(name = "TP_NIVEL", nullable = false, length = 10)
    private String tpNivel;

    @Column(name = "TP_ORIGEM", nullable = false, length = 10)
    private String tpOrigem;

    @Column(name = "DS_DESCRICAO", nullable = false, length = 500)
    private String dsDescricao;

    @Column(name = "ST_STATUS", length = 15)
    private String stStatus = "PENDENTE";

    @Column(name = "DT_GERADO", updatable = false)
    private LocalDateTime dtGerado;

    @Column(name = "DT_VISUALIZADO")
    private LocalDateTime dtVisualizado;

    @Column(name = "DT_RESOLVIDO")
    private LocalDateTime dtResolvido;

    @PrePersist
    void prePersist() {
        if (dtGerado == null) dtGerado = LocalDateTime.now();
        if (stStatus == null) stStatus = "PENDENTE";
    }
}
