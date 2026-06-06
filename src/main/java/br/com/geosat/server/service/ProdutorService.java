package br.com.geosat.server.service;

import br.com.geosat.server.dto.request.ProdutorRequest;
import br.com.geosat.server.dto.response.ProdutorResponse;
import br.com.geosat.server.exception.BusinessException;
import br.com.geosat.server.exception.ResourceNotFoundException;
import br.com.geosat.server.model.Produtor;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.repository.ProdutorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutorService {

    private final ProdutorRepository produtorRepo;

    public ProdutorService(ProdutorRepository produtorRepo) {
        this.produtorRepo = produtorRepo;
    }

    @Transactional
    public ProdutorResponse criar(ProdutorRequest request, UsuarioJava usuario) {
        if (produtorRepo.existsByNrCpf(request.nrCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }
        if (produtorRepo.existsByDsEmail(request.dsEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        Produtor p = new Produtor();
        p.setUsuario(usuario);
        p.setNmNome(request.nmNome());
        p.setNrCpf(request.nrCpf());
        p.setDsEmail(request.dsEmail());
        p.setNrTelefone(request.nrTelefone());
        p.setDsFcmToken(request.dsFcmToken());

        return ProdutorResponse.from(produtorRepo.save(p));
    }

    public List<ProdutorResponse> listarTodos() {
        return produtorRepo.findAllByAuditoria_FlAtivo("S").stream()
                .map(ProdutorResponse::from)
                .toList();
    }

    public ProdutorResponse buscarPorId(Long id, UsuarioJava usuario) {
        Produtor p = getAtivo(id);
        verificarAcesso(p, usuario);
        return ProdutorResponse.from(p);
    }

    public ProdutorResponse buscarMeu(UsuarioJava usuario) {
        return ProdutorResponse.from(
                produtorRepo.findByUsuario_IdUsuarioAndAuditoria_FlAtivo(usuario.getIdUsuario(), "S")
                        .orElseThrow(() -> new ResourceNotFoundException("Produtor não encontrado para o usuário logado")));
    }

    @Transactional
    public ProdutorResponse atualizar(Long id, ProdutorRequest request, UsuarioJava usuario) {
        Produtor p = getAtivo(id);
        verificarAcesso(p, usuario);

        p.setNmNome(request.nmNome());
        p.setNrTelefone(request.nrTelefone());
        p.setDsFcmToken(request.dsFcmToken());

        return ProdutorResponse.from(produtorRepo.save(p));
    }

    @Transactional
    public void desativar(Long id) {
        Produtor p = getAtivo(id);
        p.setFlAtivo("N");
        produtorRepo.save(p);
    }

    private Produtor getAtivo(Long id) {
        return produtorRepo.findByIdProdutorAndAuditoria_FlAtivo(id, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Produtor não encontrado: " + id));
    }

    private void verificarAcesso(Produtor p, UsuarioJava usuario) {
        if (!"ADMIN".equals(usuario.getDsRole())
                && !p.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResourceNotFoundException("Produtor não encontrado: " + p.getIdProdutor());
        }
    }
}
