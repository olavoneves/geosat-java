package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_SENSOR")
@Getter @Setter @NoArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_sensor")
    @SequenceGenerator(name = "sq_sensor", sequenceName = "sq_sensor", allocationSize = 1)
    @Column(name = "ID_SENSOR")
    private Long idSensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TALHAO", nullable = false)
    private Talhao talhao;

    @Column(name = "CD_IDENTIFICADOR_HW", nullable = false, unique = true, length = 50)
    private String cdIdentificadorHw;

    @Column(name = "DS_LOCALIZACAO", length = 200)
    private String dsLocalizacao;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "flAtivo", column = @Column(name = "FL_ATIVO", length = 1)),
        @AttributeOverride(name = "dtCriacao", column = @Column(name = "DT_INSTALACAO", updatable = false))
    })
    private Auditoria auditoria = new Auditoria();

    public String getFlAtivo() { return auditoria.getFlAtivo(); }
    public void setFlAtivo(String v) { auditoria.setFlAtivo(v); }
    public LocalDateTime getDtInstalacao() { return auditoria.getDtCriacao(); }
    public void setDtInstalacao(LocalDateTime v) { auditoria.setDtCriacao(v); }
}
