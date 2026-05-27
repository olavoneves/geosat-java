package br.com.geosat.server.repository;

import br.com.geosat.server.model.Talhao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TalhaoRepository extends JpaRepository<Talhao, Long> {

    Optional<Talhao> findByIdTalhaoAndFlAtivo(Long idTalhao, String flAtivo);

    List<Talhao> findAllByPropriedade_IdPropriedadeAndFlAtivo(Long idPropriedade, String flAtivo);
}
