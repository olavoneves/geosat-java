package br.com.geosat.server.service;

import br.com.geosat.server.dto.request.LeituraSensorRequest;
import br.com.geosat.server.dto.response.LeituraSensorResponse;
import br.com.geosat.server.exception.BusinessException;
import br.com.geosat.server.exception.ResourceNotFoundException;
import br.com.geosat.server.model.LeituraSensor;
import br.com.geosat.server.model.Sensor;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.repository.LeituraSensorRepository;
import br.com.geosat.server.repository.SensorRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LeituraSensorService {

    private final LeituraSensorRepository leituraRepo;
    private final SensorRepository sensorRepo;

    public LeituraSensorService(LeituraSensorRepository leituraRepo, SensorRepository sensorRepo) {
        this.leituraRepo = leituraRepo;
        this.sensorRepo = sensorRepo;
    }

    @Transactional
    public LeituraSensorResponse criar(LeituraSensorRequest request, UsuarioJava usuario) {
        Sensor sensor = sensorRepo.findByIdSensorAndFlAtivo(request.idSensor(), "S")
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado: " + request.idSensor()));

        verificarAcessoSensor(sensor, usuario);

        LeituraSensor l = new LeituraSensor();
        l.setSensor(sensor);
        l.setDtLeitura(request.dtLeitura());
        l.setNrTempAr(request.nrTempAr());
        l.setNrUmidadeSolo(request.nrUmidadeSolo());
        l.setNrLuminosidade(request.nrLuminosidade());

        return LeituraSensorResponse.from(leituraRepo.save(l));
    }

    public LeituraSensorResponse buscarPorId(Long id, UsuarioJava usuario) {
        LeituraSensor l = leituraRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leitura não encontrada: " + id));
        verificarAcessoSensor(l.getSensor(), usuario);
        return LeituraSensorResponse.from(l);
    }

    public Page<LeituraSensorResponse> listarPorSensor(Long idSensor, UsuarioJava usuario, Pageable pageable) {
        Sensor sensor = sensorRepo.findByIdSensorAndFlAtivo(idSensor, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado: " + idSensor));
        verificarAcessoSensor(sensor, usuario);

        return leituraRepo.findAllBySensor_IdSensorOrderByDtLeituraDesc(idSensor, pageable)
                .map(LeituraSensorResponse::from);
    }

    public LeituraSensorResponse ultimaLeitura(Long idSensor, UsuarioJava usuario) {
        Sensor sensor = sensorRepo.findByIdSensorAndFlAtivo(idSensor, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado: " + idSensor));
        verificarAcessoSensor(sensor, usuario);

        return leituraRepo.findTopBySensor_IdSensorOrderByDtLeituraDesc(idSensor)
                .map(LeituraSensorResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma leitura encontrada para o sensor: " + idSensor));
    }

    private void verificarAcessoSensor(Sensor sensor, UsuarioJava usuario) {
        if (!"ADMIN".equals(usuario.getDsRole())
                && !sensor.getTalhao().getPropriedade().getProdutor().getUsuario()
                .getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }
}
