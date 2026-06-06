package br.com.geosat.server.repository;

import br.com.geosat.server.model.Produtor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutorRepository extends JpaRepository<Produtor, Long> {

    Optional<Produtor> findByIdProdutorAndAuditoria_FlAtivo(Long idProdutor, String flAtivo);

    Optional<Produtor> findByUsuario_IdUsuarioAndAuditoria_FlAtivo(Long idUsuario, String flAtivo);

    List<Produtor> findAllByAuditoria_FlAtivo(String flAtivo);

    boolean existsByNrCpf(String nrCpf);

    boolean existsByDsEmail(String dsEmail);
}
