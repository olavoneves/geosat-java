package br.com.geosat.server.controller;

import br.com.geosat.server.dto.request.SensorRequest;
import br.com.geosat.server.dto.response.SensorResponse;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/sensores")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Sensores", description = "Gerenciamento de sensores ESP32 nos talhões")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar sensor ESP32 em um talhão")
    @ApiResponse(responseCode = "201", description = "Sensor cadastrado com sucesso")
    public ResponseEntity<EntityModel<SensorResponse>> criar(@RequestBody @Valid SensorRequest request,
                                                             HttpServletRequest req) {
        SensorResponse response = sensorService.criar(request, currentUser(req));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.idSensor()).toUri();
        return ResponseEntity.created(location).body(toModel(response, req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sensor por ID")
    @ApiResponse(responseCode = "200", description = "Sensor encontrado")
    @ApiResponse(responseCode = "404", description = "Sensor não encontrado")
    public ResponseEntity<EntityModel<SensorResponse>> buscarPorId(@PathVariable Long id,
                                                                   HttpServletRequest req) {
        return ResponseEntity.ok(toModel(sensorService.buscarPorId(id, currentUser(req)), req));
    }

    @GetMapping("/talhao/{idTalhao}")
    @Operation(summary = "Listar sensores de um talhão")
    public ResponseEntity<CollectionModel<EntityModel<SensorResponse>>> listarPorTalhao(
            @PathVariable Long idTalhao, HttpServletRequest req) {
        List<EntityModel<SensorResponse>> list = sensorService
                .listarPorTalhao(idTalhao, currentUser(req)).stream()
                .map(s -> toModel(s, req)).toList();
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(SensorController.class).listarPorTalhao(idTalhao, req)).withSelfRel()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do sensor")
    public ResponseEntity<EntityModel<SensorResponse>> atualizar(@PathVariable Long id,
                                                                 @RequestBody @Valid SensorRequest request,
                                                                 HttpServletRequest req) {
        return ResponseEntity.ok(toModel(sensorService.atualizar(id, request, currentUser(req)), req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar sensor — soft delete")
    @ApiResponse(responseCode = "204", description = "Sensor desativado")
    public ResponseEntity<Void> desativar(@PathVariable Long id, HttpServletRequest req) {
        sensorService.desativar(id, currentUser(req));
        return ResponseEntity.noContent().build();
    }

    private EntityModel<SensorResponse> toModel(SensorResponse s, HttpServletRequest req) {
        return EntityModel.of(s,
                linkTo(methodOn(SensorController.class).buscarPorId(s.idSensor(), req)).withSelfRel(),
                linkTo(methodOn(SensorController.class).listarPorTalhao(s.idTalhao(), req)).withRel("sensores-talhao"),
                linkTo(methodOn(LeituraSensorController.class).listarPorSensor(s.idSensor(), 0, 20, req)).withRel("leituras"));
    }

    private UsuarioJava currentUser(HttpServletRequest request) {
        return (UsuarioJava) request.getAttribute("currentUser");
    }
}
