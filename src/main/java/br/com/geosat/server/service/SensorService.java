package br.com.geosat.server.service;

import br.com.geosat.server.dto.request.SensorRequest;
import br.com.geosat.server.dto.response.SensorResponse;
import br.com.geosat.server.exception.BusinessException;
import br.com.geosat.server.exception.ResourceNotFoundException;
import br.com.geosat.server.model.Sensor;
import br.com.geosat.server.model.Talhao;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.repository.SensorRepository;
import br.com.geosat.server.repository.TalhaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorService {

    private final SensorRepository sensorRepo;
    private final TalhaoRepository talhaoRepo;

    public SensorService(SensorRepository sensorRepo, TalhaoRepository talhaoRepo) {
        this.sensorRepo = sensorRepo;
        this.talhaoRepo = talhaoRepo;
    }

    @Transactional
    public SensorResponse criar(SensorRequest request, UsuarioJava usuario) {
        Talhao talhao = getTalhaoComAcesso(request.idTalhao(), usuario);

        if (sensorRepo.existsByCdIdentificadorHw(request.cdIdentificadorHw())) {
            throw new BusinessException("Identificador de hardware já cadastrado: " + request.cdIdentificadorHw());
        }

        Sensor s = new Sensor();
        s.setTalhao(talhao);
        s.setCdIdentificadorHw(request.cdIdentificadorHw());
        s.setDsLocalizacao(request.dsLocalizacao());

        return SensorResponse.from(sensorRepo.save(s));
    }

    public SensorResponse buscarPorId(Long id, UsuarioJava usuario) {
        Sensor s = getAtivo(id);
        verificarAcesso(s, usuario);
        return SensorResponse.from(s);
    }

    public List<SensorResponse> listarPorTalhao(Long idTalhao, UsuarioJava usuario) {
        getTalhaoComAcesso(idTalhao, usuario);
        return sensorRepo.findAllByTalhao_IdTalhaoAndAuditoria_FlAtivo(idTalhao, "S").stream()
                .map(SensorResponse::from)
                .toList();
    }

    @Transactional
    public SensorResponse atualizar(Long id, SensorRequest request, UsuarioJava usuario) {
        Sensor s = getAtivo(id);
        verificarAcesso(s, usuario);

        s.setDsLocalizacao(request.dsLocalizacao());

        return SensorResponse.from(sensorRepo.save(s));
    }

    @Transactional
    public void desativar(Long id, UsuarioJava usuario) {
        Sensor s = getAtivo(id);
        verificarAcesso(s, usuario);
        s.setFlAtivo("N");
        sensorRepo.save(s);
    }

    private Sensor getAtivo(Long id) {
        return sensorRepo.findByIdSensorAndAuditoria_FlAtivo(id, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado: " + id));
    }

    private Talhao getTalhaoComAcesso(Long idTalhao, UsuarioJava usuario) {
        Talhao t = talhaoRepo.findByIdTalhaoAndAuditoria_FlAtivo(idTalhao, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Talhão não encontrado: " + idTalhao));
        verificarAcessoTalhao(t, usuario);
        return t;
    }

    private void verificarAcesso(Sensor s, UsuarioJava usuario) {
        verificarAcessoTalhao(s.getTalhao(), usuario);
    }

    private void verificarAcessoTalhao(Talhao t, UsuarioJava usuario) {
        if (!"ADMIN".equals(usuario.getDsRole())
                && !t.getPropriedade().getProdutor().getUsuario().getIdUsuario()
                .equals(usuario.getIdUsuario())) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }
}
