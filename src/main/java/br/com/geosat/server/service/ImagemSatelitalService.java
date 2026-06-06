package br.com.geosat.server.service;

import br.com.geosat.server.dto.request.ErroImagemRequest;
import br.com.geosat.server.dto.request.ImagemSatelitalRequest;
import br.com.geosat.server.dto.request.ProcessarImagemRequest;
import br.com.geosat.server.dto.response.ImagemSatelitalResponse;
import br.com.geosat.server.exception.BusinessException;
import br.com.geosat.server.exception.ResourceNotFoundException;
import br.com.geosat.server.model.ImagemSatelital;
import br.com.geosat.server.model.Talhao;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.repository.ImagemSatelitalRepository;
import br.com.geosat.server.repository.TalhaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ImagemSatelitalService {

    private final ImagemSatelitalRepository imagemRepo;
    private final TalhaoRepository talhaoRepo;

    public ImagemSatelitalService(ImagemSatelitalRepository imagemRepo, TalhaoRepository talhaoRepo) {
        this.imagemRepo = imagemRepo;
        this.talhaoRepo = talhaoRepo;
    }

    @Transactional
    public ImagemSatelitalResponse criar(ImagemSatelitalRequest request, UsuarioJava usuario) {
        Talhao talhao = getTalhaoComAcesso(request.idTalhao(), usuario);

        ImagemSatelital i = new ImagemSatelital();
        i.setTalhao(talhao);
        i.setDtCaptura(request.dtCaptura());
        i.setDsFonte(request.dsFonte());
        i.setDsStatusProc("PENDENTE");

        return ImagemSatelitalResponse.from(imagemRepo.save(i));
    }

    public ImagemSatelitalResponse buscarPorId(Long id, UsuarioJava usuario) {
        ImagemSatelital i = getImagem(id);
        verificarAcesso(i, usuario);
        return ImagemSatelitalResponse.from(i);
    }

    public Page<ImagemSatelitalResponse> listarPorTalhao(Long idTalhao, UsuarioJava usuario, Pageable pageable) {
        getTalhaoComAcesso(idTalhao, usuario);
        return imagemRepo.findAllByTalhao_IdTalhaoOrderByDtCapturaDesc(idTalhao, pageable)
                .map(ImagemSatelitalResponse::from);
    }

    @Transactional
    public ImagemSatelitalResponse processar(Long id, ProcessarImagemRequest request, UsuarioJava usuario) {
        ImagemSatelital i = getImagem(id);
        verificarAcesso(i, usuario);

        if (!"PENDENTE".equals(i.getDsStatusProc())) {
            throw new BusinessException("Imagem não está com status PENDENTE");
        }

        i.setNrNdvi(request.nrNdvi());
        i.setDsStatusProc("PROCESSADO");
        i.setDtProcessado(LocalDateTime.now());

        return ImagemSatelitalResponse.from(imagemRepo.save(i));
    }

    @Transactional
    public ImagemSatelitalResponse marcarErro(Long id, ErroImagemRequest request, UsuarioJava usuario) {
        ImagemSatelital i = getImagem(id);
        verificarAcesso(i, usuario);

        i.setDsStatusProc("ERRO");
        i.setDsErro(request.dsErro());

        return ImagemSatelitalResponse.from(imagemRepo.save(i));
    }

    private ImagemSatelital getImagem(Long id) {
        return imagemRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem satelital não encontrada: " + id));
    }

    private Talhao getTalhaoComAcesso(Long idTalhao, UsuarioJava usuario) {
        Talhao t = talhaoRepo.findByIdTalhaoAndAuditoria_FlAtivo(idTalhao, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Talhão não encontrado: " + idTalhao));
        verificarAcessoTalhao(t, usuario);
        return t;
    }

    private void verificarAcesso(ImagemSatelital i, UsuarioJava usuario) {
        verificarAcessoTalhao(i.getTalhao(), usuario);
    }

    private void verificarAcessoTalhao(Talhao t, UsuarioJava usuario) {
        if (!"ADMIN".equals(usuario.getDsRole())
                && !t.getPropriedade().getProdutor().getUsuario().getIdUsuario()
                .equals(usuario.getIdUsuario())) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }
}
