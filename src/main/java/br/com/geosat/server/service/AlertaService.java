package br.com.geosat.server.service;

import br.com.geosat.server.dto.response.AlertaResponse;
import br.com.geosat.server.exception.BusinessException;
import br.com.geosat.server.exception.ResourceNotFoundException;
import br.com.geosat.server.model.Alerta;
import br.com.geosat.server.model.Produtor;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.repository.AlertaRepository;
import br.com.geosat.server.repository.ProdutorRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepo;
    private final ProdutorRepository produtorRepo;

    public AlertaService(AlertaRepository alertaRepo, ProdutorRepository produtorRepo) {
        this.alertaRepo = alertaRepo;
        this.produtorRepo = produtorRepo;
    }

    public Page<AlertaResponse> listarTodos(String status, Pageable pageable) {
        if (status != null) {
            return alertaRepo.findAllByStStatus(status, pageable).map(AlertaResponse::from);
        }
        return alertaRepo.findAll(pageable).map(AlertaResponse::from);
    }

    public AlertaResponse buscarPorId(Long id, UsuarioJava usuario) {
        Alerta a = getAlerta(id);
        verificarAcesso(a, usuario);
        return AlertaResponse.from(a);
    }

    public List<AlertaResponse> listarPorTalhao(Long idTalhao, UsuarioJava usuario) {
        return alertaRepo.findAllByTalhao_IdTalhao(idTalhao).stream()
                .filter(a -> temAcesso(a, usuario))
                .map(AlertaResponse::from)
                .toList();
    }

    public List<AlertaResponse> listarMeus(UsuarioJava usuario) {
        Produtor produtor = getProdutorDoUsuario(usuario);
        return alertaRepo.findAllByProdutorId(produtor.getIdProdutor()).stream()
                .map(AlertaResponse::from)
                .toList();
    }

    public List<AlertaResponse> listarMeusPendentes(UsuarioJava usuario) {
        Produtor produtor = getProdutorDoUsuario(usuario);
        return alertaRepo.findPendentesByProdutorId(produtor.getIdProdutor()).stream()
                .map(AlertaResponse::from)
                .toList();
    }

    @Transactional
    public AlertaResponse visualizar(Long id, UsuarioJava usuario) {
        Alerta a = getAlerta(id);
        verificarAcesso(a, usuario);

        if ("RESOLVIDO".equals(a.getStStatus())) {
            throw new BusinessException("Alerta já está resolvido");
        }

        a.setStStatus("VISUALIZADO");
        return AlertaResponse.from(alertaRepo.save(a));
    }

    @Transactional
    public AlertaResponse resolver(Long id, UsuarioJava usuario) {
        Alerta a = getAlerta(id);
        verificarAcesso(a, usuario);

        a.setStStatus("RESOLVIDO");
        return AlertaResponse.from(alertaRepo.save(a));
    }

    @Transactional
    public AlertaResponse reabrir(Long id) {
        Alerta a = getAlerta(id);

        if (!"RESOLVIDO".equals(a.getStStatus())) {
            throw new BusinessException("Apenas alertas com status RESOLVIDO podem ser reabertos");
        }

        a.setStStatus("PENDENTE");
        return AlertaResponse.from(alertaRepo.save(a));
    }

    private Alerta getAlerta(Long id) {
        return alertaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado: " + id));
    }

    private void verificarAcesso(Alerta a, UsuarioJava usuario) {
        if (!temAcesso(a, usuario)) {
            throw new ResourceNotFoundException("Alerta não encontrado: " + a.getIdAlerta());
        }
    }

    private boolean temAcesso(Alerta a, UsuarioJava usuario) {
        if ("ADMIN".equals(usuario.getDsRole())) return true;
        return a.getTalhao().getPropriedade().getProdutor().getUsuario()
                .getIdUsuario().equals(usuario.getIdUsuario());
    }

    private Produtor getProdutorDoUsuario(UsuarioJava usuario) {
        return produtorRepo.findByUsuario_IdUsuarioAndAuditoria_FlAtivo(usuario.getIdUsuario(), "S")
                .orElseThrow(() -> new ResourceNotFoundException("Produtor não encontrado para o usuário logado"));
    }
}
