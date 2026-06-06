package br.com.geosat.server.repository;

import br.com.geosat.server.model.Propriedade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {

    Optional<Propriedade> findByIdPropriedadeAndAuditoria_FlAtivo(Long idPropriedade, String flAtivo);

    List<Propriedade> findAllByAuditoria_FlAtivo(String flAtivo);

    List<Propriedade> findAllByProdutor_IdProdutorAndAuditoria_FlAtivo(Long idProdutor, String flAtivo);
}
