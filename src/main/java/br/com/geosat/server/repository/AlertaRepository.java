package br.com.geosat.server.repository;

import br.com.geosat.server.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    Page<Alerta> findAllByStStatus(String stStatus, Pageable pageable);

    Page<Alerta> findAll(Pageable pageable);

    List<Alerta> findAllByTalhao_IdTalhao(Long idTalhao);

    @Query("""
        SELECT a FROM Alerta a
        WHERE a.talhao.propriedade.produtor.idProdutor = :idProdutor
        ORDER BY a.dtGerado DESC
        """)
    List<Alerta> findAllByProdutorId(@Param("idProdutor") Long idProdutor);

    @Query("""
        SELECT a FROM Alerta a
        WHERE a.talhao.propriedade.produtor.idProdutor = :idProdutor
          AND a.stStatus = 'PENDENTE'
        ORDER BY a.dtGerado DESC
        """)
    List<Alerta> findPendentesByProdutorId(@Param("idProdutor") Long idProdutor);
}
