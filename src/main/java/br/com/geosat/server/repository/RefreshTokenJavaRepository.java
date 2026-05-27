package br.com.geosat.server.repository;

import br.com.geosat.server.model.RefreshTokenJava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenJavaRepository extends JpaRepository<RefreshTokenJava, Long> {

    Optional<RefreshTokenJava> findByDsToken(String dsToken);

    @Modifying
    @Query("UPDATE RefreshTokenJava t SET t.flRevogado = 'S' WHERE t.usuario.idUsuario = :idUsuario AND t.flRevogado = 'N'")
    void revokeAllActiveByUsuarioId(@Param("idUsuario") Long idUsuario);
}
