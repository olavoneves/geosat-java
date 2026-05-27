package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_LOG_ALERTA")
@Getter @Setter @NoArgsConstructor
public class LogAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_log_alerta")
    @SequenceGenerator(name = "sq_log_alerta", sequenceName = "sq_log_alerta", allocationSize = 1)
    @Column(name = "ID_LOG")
    private Long idLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALERTA", nullable = false)
    private Alerta alerta;

    @Column(name = "DS_ACAO", nullable = false, length = 15)
    private String dsAcao;

    @Column(name = "DS_ORIGEM", nullable = false, length = 15)
    private String dsOrigem;

    @Column(name = "DS_OBSERVACAO", length = 500)
    private String dsObservacao;

    @Column(name = "DT_EVENTO", updatable = false)
    private LocalDateTime dtEvento;

    @PrePersist
    void prePersist() {
        if (dtEvento == null) dtEvento = LocalDateTime.now();
    }
}
