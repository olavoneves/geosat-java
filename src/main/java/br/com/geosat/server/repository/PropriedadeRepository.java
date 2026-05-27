package br.com.geosat.server.repository;

import br.com.geosat.server.model.Propriedade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {

    Optional<Propriedade> findByIdPropriedadeAndFlAtiva(Long idPropriedade, String flAtiva);

    List<Propriedade> findAllByFlAtiva(String flAtiva);

    List<Propriedade> findAllByProdutor_IdProdutorAndFlAtiva(Long idProdutor, String flAtiva);
}
