package br.com.geosat.server.repository;

import br.com.geosat.server.model.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {

    Optional<Configuracao> findByTalhao_IdTalhao(Long idTalhao);
}
