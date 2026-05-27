package br.com.geosat.server.repository;

import br.com.geosat.server.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findByIdSensorAndFlAtivo(Long idSensor, String flAtivo);

    List<Sensor> findAllByTalhao_IdTalhaoAndFlAtivo(Long idTalhao, String flAtivo);

    boolean existsByCdIdentificadorHw(String cdIdentificadorHw);
}
