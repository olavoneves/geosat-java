package br.com.geosat.server.repository;

import br.com.geosat.server.model.LeituraSensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeituraSensorRepository extends JpaRepository<LeituraSensor, Long> {

    Page<LeituraSensor> findAllBySensor_IdSensorOrderByDtLeituraDesc(Long idSensor, Pageable pageable);

    Optional<LeituraSensor> findTopBySensor_IdSensorOrderByDtLeituraDesc(Long idSensor);
}
