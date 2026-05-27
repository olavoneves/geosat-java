package br.com.geosat.server.repository;

import br.com.geosat.server.model.UsuarioJava;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioJavaRepository extends JpaRepository<UsuarioJava, Long> {

    Optional<UsuarioJava> findByDsEmail(String dsEmail);

    Optional<UsuarioJava> findByIdUsuarioAndFlAtivo(Long idUsuario, String flAtivo);

    List<UsuarioJava> findAllByFlAtivo(String flAtivo);

    boolean existsByDsEmail(String dsEmail);
}
