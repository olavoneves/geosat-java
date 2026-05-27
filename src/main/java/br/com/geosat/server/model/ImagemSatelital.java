package br.com.geosat.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GST_IMAGEM_SATELITAL")
@Getter @Setter @NoArgsConstructor
public class ImagemSatelital {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_imagem_sat")
    @SequenceGenerator(name = "sq_imagem_sat", sequenceName = "sq_imagem_sat", allocationSize = 1)
    @Column(name = "ID_IMAGEM")
    private Long idImagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TALHAO", nullable = false)
    private Talhao talhao;

    @Column(name = "DT_CAPTURA", nullable = false)
    private LocalDate dtCaptura;

    @Column(name = "NR_NDVI", precision = 4, scale = 3)
    private BigDecimal nrNdvi;

    @Column(name = "DS_FONTE", nullable = false, length = 10)
    private String dsFonte;

    @Column(name = "DS_STATUS_PROC", length = 15)
    private String dsStatusProc = "PENDENTE";

    @Column(name = "DS_ERRO", length = 500)
    private String dsErro;

    @Column(name = "DT_PROCESSADO")
    private LocalDateTime dtProcessado;
}
