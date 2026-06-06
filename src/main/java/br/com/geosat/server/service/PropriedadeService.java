package br.com.geosat.server.service;

import br.com.geosat.server.dto.request.PropriedadeRequest;
import br.com.geosat.server.dto.response.PropriedadeResponse;
import br.com.geosat.server.exception.ResourceNotFoundException;
import br.com.geosat.server.model.Produtor;
import br.com.geosat.server.model.Propriedade;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.repository.ProdutorRepository;
import br.com.geosat.server.repository.PropriedadeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropriedadeService {

    private final PropriedadeRepository propriedadeRepo;
    private final ProdutorRepository produtorRepo;

    public PropriedadeService(PropriedadeRepository propriedadeRepo,
                              ProdutorRepository produtorRepo) {
        this.propriedadeRepo = propriedadeRepo;
        this.produtorRepo = produtorRepo;
    }

    @Transactional
    public PropriedadeResponse criar(PropriedadeRequest request, UsuarioJava usuario) {
        Produtor produtor = produtorRepo.findByUsuario_IdUsuarioAndAuditoria_FlAtivo(usuario.getIdUsuario(), "S")
                .orElseThrow(() -> new ResourceNotFoundException("Produtor não encontrado para o usuário logado"));

        Propriedade p = new Propriedade();
        p.setProdutor(produtor);
        p.setNmNome(request.nmNome());
        p.setNmMunicipio(request.nmMunicipio());
        p.setSgEstado(request.sgEstado());
        p.setNrAreaHa(request.nrAreaHa());

        return PropriedadeResponse.from(propriedadeRepo.save(p));
    }

    public List<PropriedadeResponse> listarTodas() {
        return propriedadeRepo.findAllByAuditoria_FlAtivo("S").stream()
                .map(PropriedadeResponse::from)
                .toList();
    }

    public PropriedadeResponse buscarPorId(Long id, UsuarioJava usuario) {
        Propriedade p = getAtiva(id);
        verificarAcesso(p, usuario);
        return PropriedadeResponse.from(p);
    }

    public List<PropriedadeResponse> listarPorProdutor(Long idProdutor, UsuarioJava usuario) {
        Produtor produtor = produtorRepo.findByIdProdutorAndAuditoria_FlAtivo(idProdutor, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Produtor não encontrado: " + idProdutor));
        verificarAcessoProdutor(produtor, usuario);

        return propriedadeRepo.findAllByProdutor_IdProdutorAndAuditoria_FlAtivo(idProdutor, "S").stream()
                .map(PropriedadeResponse::from)
                .toList();
    }

    @Transactional
    public PropriedadeResponse atualizar(Long id, PropriedadeRequest request, UsuarioJava usuario) {
        Propriedade p = getAtiva(id);
        verificarAcesso(p, usuario);

        p.setNmNome(request.nmNome());
        p.setNmMunicipio(request.nmMunicipio());
        p.setSgEstado(request.sgEstado());
        p.setNrAreaHa(request.nrAreaHa());

        return PropriedadeResponse.from(propriedadeRepo.save(p));
    }

    @Transactional
    public void desativar(Long id, UsuarioJava usuario) {
        Propriedade p = getAtiva(id);
        verificarAcesso(p, usuario);
        p.setFlAtiva("N");
        propriedadeRepo.save(p);
    }

    private Propriedade getAtiva(Long id) {
        return propriedadeRepo.findByIdPropriedadeAndAuditoria_FlAtivo(id, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada: " + id));
    }

    private void verificarAcesso(Propriedade p, UsuarioJava usuario) {
        verificarAcessoProdutor(p.getProdutor(), usuario);
    }

    private void verificarAcessoProdutor(Produtor produtor, UsuarioJava usuario) {
        if (!"ADMIN".equals(usuario.getDsRole())
                && !produtor.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }
}
