package br.com.geosat.server.repository;

import br.com.geosat.server.model.ImagemSatelital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemSatelitalRepository extends JpaRepository<ImagemSatelital, Long> {

    Page<ImagemSatelital> findAllByTalhao_IdTalhaoOrderByDtCapturaDesc(Long idTalhao, Pageable pageable);
}
