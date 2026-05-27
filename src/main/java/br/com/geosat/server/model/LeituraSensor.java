package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_LEITURA_SENSOR")
@Getter @Setter @NoArgsConstructor
public class LeituraSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_leitura_sensor")
    @SequenceGenerator(name = "sq_leitura_sensor", sequenceName = "sq_leitura_sensor", allocationSize = 1)
    @Column(name = "ID_LEITURA")
    private Long idLeitura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SENSOR", nullable = false)
    private Sensor sensor;

    @Column(name = "DT_LEITURA", nullable = false)
    private LocalDateTime dtLeitura;

    @Column(name = "NR_TEMP_AR", nullable = false, precision = 5, scale = 2)
    private BigDecimal nrTempAr;

    @Column(name = "NR_UMIDADE_SOLO", nullable = false, precision = 5, scale = 2)
    private BigDecimal nrUmidadeSolo;

    @Column(name = "NR_LUMINOSIDADE", precision = 9, scale = 2)
    private BigDecimal nrLuminosidade;

    @Column(name = "FL_TRANSMITIDA", length = 1)
    private String flTransmitida = "N";

    @Column(name = "DT_RECEBIDA", updatable = false)
    private LocalDateTime dtRecebida;

    @PrePersist
    void prePersist() {
        if (dtRecebida == null) dtRecebida = LocalDateTime.now();
        if (flTransmitida == null) flTransmitida = "N";
    }
}
