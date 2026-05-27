package br.com.geosat.server.repository;

import br.com.geosat.server.model.LogAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogAlertaRepository extends JpaRepository<LogAlerta, Long> {

    List<LogAlerta> findAllByAlerta_IdAlertaOrderByDtEventoDesc(Long idAlerta);
}
