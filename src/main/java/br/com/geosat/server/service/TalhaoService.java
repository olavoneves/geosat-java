package br.com.geosat.server.service;

import br.com.geosat.server.dto.request.TalhaoRequest;
import br.com.geosat.server.dto.response.TalhaoResponse;
import br.com.geosat.server.exception.ResourceNotFoundException;
import br.com.geosat.server.model.Propriedade;
import br.com.geosat.server.model.Talhao;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.repository.PropriedadeRepository;
import br.com.geosat.server.repository.TalhaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TalhaoService {

    private final TalhaoRepository talhaoRepo;
    private final PropriedadeRepository propriedadeRepo;

    public TalhaoService(TalhaoRepository talhaoRepo, PropriedadeRepository propriedadeRepo) {
        this.talhaoRepo = talhaoRepo;
        this.propriedadeRepo = propriedadeRepo;
    }

    @Transactional
    public TalhaoResponse criar(TalhaoRequest request, UsuarioJava usuario) {
        Propriedade propriedade = getPropriedadeComAcesso(request.idPropriedade(), usuario);

        Talhao t = new Talhao();
        t.setPropriedade(propriedade);
        t.setNmNome(request.nmNome());
        t.setDsCultura(request.dsCultura());
        t.setNrAreaHa(request.nrAreaHa());

        return TalhaoResponse.from(talhaoRepo.save(t));
    }

    public TalhaoResponse buscarPorId(Long id, UsuarioJava usuario) {
        Talhao t = getAtivo(id);
        verificarAcesso(t, usuario);
        return TalhaoResponse.from(t);
    }

    public List<TalhaoResponse> listarPorPropriedade(Long idPropriedade, UsuarioJava usuario) {
        getPropriedadeComAcesso(idPropriedade, usuario);
        return talhaoRepo.findAllByPropriedade_IdPropriedadeAndFlAtivo(idPropriedade, "S").stream()
                .map(TalhaoResponse::from)
                .toList();
    }

    @Transactional
    public TalhaoResponse atualizar(Long id, TalhaoRequest request, UsuarioJava usuario) {
        Talhao t = getAtivo(id);
        verificarAcesso(t, usuario);

        t.setNmNome(request.nmNome());
        t.setDsCultura(request.dsCultura());
        t.setNrAreaHa(request.nrAreaHa());

        return TalhaoResponse.from(talhaoRepo.save(t));
    }

    @Transactional
    public void desativar(Long id, UsuarioJava usuario) {
        Talhao t = getAtivo(id);
        verificarAcesso(t, usuario);
        t.setFlAtivo("N");
        talhaoRepo.save(t);
    }

    private Talhao getAtivo(Long id) {
        return talhaoRepo.findByIdTalhaoAndFlAtivo(id, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Talhão não encontrado: " + id));
    }

    private Propriedade getPropriedadeComAcesso(Long idPropriedade, UsuarioJava usuario) {
        Propriedade p = propriedadeRepo.findByIdPropriedadeAndFlAtiva(idPropriedade, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada: " + idPropriedade));
        if (!"ADMIN".equals(usuario.getDsRole())
                && !p.getProdutor().getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResourceNotFoundException("Propriedade não encontrada: " + idPropriedade);
        }
        return p;
    }

    private void verificarAcesso(Talhao t, UsuarioJava usuario) {
        if (!"ADMIN".equals(usuario.getDsRole())
                && !t.getPropriedade().getProdutor().getUsuario().getIdUsuario()
                .equals(usuario.getIdUsuario())) {
            throw new ResourceNotFoundException("Talhão não encontrado: " + t.getIdTalhao());
        }
    }
}
