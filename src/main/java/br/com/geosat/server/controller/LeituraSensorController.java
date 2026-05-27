package br.com.geosat.server.controller;

import br.com.geosat.server.dto.request.LeituraSensorRequest;
import br.com.geosat.server.dto.response.LeituraSensorResponse;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.service.LeituraSensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/leituras")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Leituras de Sensor", description = "Registro e consulta de leituras dos sensores ESP32")
public class LeituraSensorController {

    private final LeituraSensorService leituraService;

    public LeituraSensorController(LeituraSensorService leituraService) {
        this.leituraService = leituraService;
    }

    @PostMapping
    @Operation(summary = "Registrar leitura do sensor — trigger pode gerar alerta automaticamente")
    @ApiResponse(responseCode = "201", description = "Leitura registrada com sucesso")
    public ResponseEntity<EntityModel<LeituraSensorResponse>> criar(@RequestBody @Valid LeituraSensorRequest request,
                                                                    HttpServletRequest req) {
        LeituraSensorResponse response = leituraService.criar(request, currentUser(req));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.idLeitura()).toUri();
        return ResponseEntity.created(location).body(toModel(response, req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar leitura por ID")
    @ApiResponse(responseCode = "200", description = "Leitura encontrada")
    @ApiResponse(responseCode = "404", description = "Leitura não encontrada")
    public ResponseEntity<EntityModel<LeituraSensorResponse>> buscarPorId(@PathVariable Long id,
                                                                          HttpServletRequest req) {
        return ResponseEntity.ok(toModel(leituraService.buscarPorId(id, currentUser(req)), req));
    }

    @GetMapping("/sensor/{idSensor}")
    @Operation(summary = "Listar leituras de um sensor (paginado)")
    public ResponseEntity<Page<LeituraSensorResponse>> listarPorSensor(
            @PathVariable Long idSensor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest req) {
        return ResponseEntity.ok(leituraService.listarPorSensor(idSensor, currentUser(req),
                PageRequest.of(page, size)));
    }

    @GetMapping("/sensor/{idSensor}/last")
    @Operation(summary = "Obter última leitura do sensor")
    @ApiResponse(responseCode = "200", description = "Última leitura encontrada")
    @ApiResponse(responseCode = "404", description = "Nenhuma leitura encontrada")
    public ResponseEntity<EntityModel<LeituraSensorResponse>> ultimaLeitura(@PathVariable Long idSensor,
                                                                             HttpServletRequest req) {
        return ResponseEntity.ok(toModel(leituraService.ultimaLeitura(idSensor, currentUser(req)), req));
    }

    private EntityModel<LeituraSensorResponse> toModel(LeituraSensorResponse l, HttpServletRequest req) {
        return EntityModel.of(l,
                linkTo(methodOn(LeituraSensorController.class).buscarPorId(l.idLeitura(), req)).withSelfRel(),
                linkTo(methodOn(LeituraSensorController.class).listarPorSensor(l.idSensor(), 0, 20, req)).withRel("leituras-sensor"));
    }

    private UsuarioJava currentUser(HttpServletRequest request) {
        return (UsuarioJava) request.getAttribute("currentUser");
    }
}
