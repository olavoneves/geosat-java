package br.com.geosat.server.repository;

import br.com.geosat.server.model.AccessTokenJava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccessTokenJavaRepository extends JpaRepository<AccessTokenJava, Long> {

    Optional<AccessTokenJava> findByDsToken(String dsToken);

    @Query("SELECT t FROM AccessTokenJava t JOIN FETCH t.usuario WHERE t.dsToken = :dsToken")
    Optional<AccessTokenJava> findByDsTokenWithUsuario(@Param("dsToken") String dsToken);

    @Modifying
    @Query("UPDATE AccessTokenJava t SET t.flRevogado = 'S' WHERE t.usuario.idUsuario = :idUsuario AND t.flRevogado = 'N'")
    void revokeAllActiveByUsuarioId(@Param("idUsuario") Long idUsuario);
}
