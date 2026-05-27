package br.com.geosat.server.service;

import br.com.geosat.server.dto.request.ConfiguracaoRequest;
import br.com.geosat.server.dto.response.ConfiguracaoResponse;
import br.com.geosat.server.exception.ResourceNotFoundException;
import br.com.geosat.server.model.Configuracao;
import br.com.geosat.server.model.Talhao;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.repository.ConfiguracaoRepository;
import br.com.geosat.server.repository.TalhaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracaoService {

    private final ConfiguracaoRepository configuracaoRepo;
    private final TalhaoRepository talhaoRepo;

    public ConfiguracaoService(ConfiguracaoRepository configuracaoRepo, TalhaoRepository talhaoRepo) {
        this.configuracaoRepo = configuracaoRepo;
        this.talhaoRepo = talhaoRepo;
    }

    public ConfiguracaoResponse buscarPorTalhao(Long idTalhao, UsuarioJava usuario) {
        verificarAcessoTalhao(idTalhao, usuario);

        return ConfiguracaoResponse.from(
                configuracaoRepo.findByTalhao_IdTalhao(idTalhao)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Configuração não encontrada para o talhão: " + idTalhao)));
    }

    @Transactional
    public ConfiguracaoResponse atualizar(Long id, ConfiguracaoRequest request, UsuarioJava usuario) {
        Configuracao c = configuracaoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuração não encontrada: " + id));

        verificarAcessoTalhao(c.getTalhao().getIdTalhao(), usuario);

        c.setNrThresholdUmidMin(request.nrThresholdUmidMin());
        c.setNrThresholdNdviMin(request.nrThresholdNdviMin());
        c.setNrJanelaFusaoHoras(request.nrJanelaFusaoHoras());

        return ConfiguracaoResponse.from(configuracaoRepo.save(c));
    }

    private void verificarAcessoTalhao(Long idTalhao, UsuarioJava usuario) {
        Talhao t = talhaoRepo.findByIdTalhaoAndFlAtivo(idTalhao, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Talhão não encontrado: " + idTalhao));

        if (!"ADMIN".equals(usuario.getDsRole())
                && !t.getPropriedade().getProdutor().getUsuario().getIdUsuario()
                .equals(usuario.getIdUsuario())) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }
}
