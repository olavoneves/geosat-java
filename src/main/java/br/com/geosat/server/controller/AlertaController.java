package br.com.geosat.server.controller;

import br.com.geosat.server.dto.response.AlertaResponse;
import br.com.geosat.server.exception.UnauthorizedException;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/alertas")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Alertas", description = "Consulta e gestão de alertas gerados pelos triggers Oracle")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os alertas (ADMIN) — filtro opcional por status")
    public ResponseEntity<Page<AlertaResponse>> listarTodos(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest req) {
        requireAdmin(req);
        return ResponseEntity.ok(alertaService.listarTodos(status, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar alerta por ID")
    @ApiResponse(responseCode = "200", description = "Alerta encontrado")
    @ApiResponse(responseCode = "404", description = "Alerta não encontrado")
    public ResponseEntity<EntityModel<AlertaResponse>> buscarPorId(@PathVariable Long id,
                                                                   HttpServletRequest req) {
        return ResponseEntity.ok(toModel(alertaService.buscarPorId(id, currentUser(req)), req));
    }

    @GetMapping("/talhao/{idTalhao}")
    @Operation(summary = "Listar alertas de um talhão")
    public ResponseEntity<CollectionModel<EntityModel<AlertaResponse>>> listarPorTalhao(
            @PathVariable Long idTalhao, HttpServletRequest req) {
        List<EntityModel<AlertaResponse>> list = alertaService
                .listarPorTalhao(idTalhao, currentUser(req)).stream()
                .map(a -> toModel(a, req)).toList();
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(AlertaController.class).listarPorTalhao(idTalhao, req)).withSelfRel()));
    }

    @GetMapping("/produtor/me")
    @Operation(summary = "Listar todos os alertas do produtor logado")
    public ResponseEntity<CollectionModel<EntityModel<AlertaResponse>>> listarMeus(HttpServletRequest req) {
        List<EntityModel<AlertaResponse>> list = alertaService.listarMeus(currentUser(req)).stream()
                .map(a -> toModel(a, req)).toList();
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(AlertaController.class).listarMeus(req)).withSelfRel()));
    }

    @GetMapping("/produtor/me/pendentes")
    @Operation(summary = "Listar alertas PENDENTES do produtor logado")
    public ResponseEntity<CollectionModel<EntityModel<AlertaResponse>>> listarMeusPendentes(HttpServletRequest req) {
        List<EntityModel<AlertaResponse>> list = alertaService.listarMeusPendentes(currentUser(req)).stream()
                .map(a -> toModel(a, req)).toList();
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(AlertaController.class).listarMeusPendentes(req)).withSelfRel()));
    }

    @PatchMapping("/{id}/visualizar")
    @Operation(summary = "Marcar alerta como VISUALIZADO")
    @ApiResponse(responseCode = "200", description = "Alerta visualizado")
    public ResponseEntity<EntityModel<AlertaResponse>> visualizar(@PathVariable Long id,
                                                                  HttpServletRequest req) {
        return ResponseEntity.ok(toModel(alertaService.visualizar(id, currentUser(req)), req));
    }

    @PatchMapping("/{id}/resolver")
    @Operation(summary = "Marcar alerta como RESOLVIDO")
    @ApiResponse(responseCode = "200", description = "Alerta resolvido")
    public ResponseEntity<EntityModel<AlertaResponse>> resolver(@PathVariable Long id,
                                                                HttpServletRequest req) {
        return ResponseEntity.ok(toModel(alertaService.resolver(id, currentUser(req)), req));
    }

    @PatchMapping("/{id}/reabrir")
    @Operation(summary = "Reabrir alerta RESOLVIDO (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Alerta reaberto")
    public ResponseEntity<EntityModel<AlertaResponse>> reabrir(@PathVariable Long id,
                                                               HttpServletRequest req) {
        requireAdmin(req);
        return ResponseEntity.ok(toModel(alertaService.reabrir(id), req));
    }

    private EntityModel<AlertaResponse> toModel(AlertaResponse a, HttpServletRequest req) {
        return EntityModel.of(a,
                linkTo(methodOn(AlertaController.class).buscarPorId(a.idAlerta(), req)).withSelfRel(),
                linkTo(methodOn(AlertaController.class).listarPorTalhao(a.idTalhao(), req)).withRel("alertas-talhao"));
    }

    private UsuarioJava currentUser(HttpServletRequest request) {
        return (UsuarioJava) request.getAttribute("currentUser");
    }

    private void requireAdmin(HttpServletRequest request) {
        UsuarioJava user = currentUser(request);
        if (!"ADMIN".equals(user.getDsRole())) {
            throw new UnauthorizedException("Acesso restrito a administradores");
        }
    }
}
