package br.com.geosat.server.dto.response;

import br.com.geosat.server.model.ImagemSatelital;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ImagemSatelitalResponse(
        Long idImagem,
        Long idTalhao,
        LocalDate dtCaptura,
        BigDecimal nrNdvi,
        String dsFonte,
        String dsStatusProc,
        String dsErro,
        LocalDateTime dtProcessado
) {
    public static ImagemSatelitalResponse from(ImagemSatelital i) {
        return new ImagemSatelitalResponse(i.getIdImagem(), i.getTalhao().getIdTalhao(),
                i.getDtCaptura(), i.getNrNdvi(), i.getDsFonte(),
                i.getDsStatusProc(), i.getDsErro(), i.getDtProcessado());
    }
}
