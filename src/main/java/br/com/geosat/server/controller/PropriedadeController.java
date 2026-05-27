package br.com.geosat.server.controller;

import br.com.geosat.server.dto.request.PropriedadeRequest;
import br.com.geosat.server.dto.response.PropriedadeResponse;
import br.com.geosat.server.exception.UnauthorizedException;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.service.PropriedadeService;
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
@RequestMapping("/propriedades")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Propriedades", description = "Gerenciamento de propriedades rurais")
public class PropriedadeController {

    private final PropriedadeService propriedadeService;

    public PropriedadeController(PropriedadeService propriedadeService) {
        this.propriedadeService = propriedadeService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar propriedade para o produtor logado")
    @ApiResponse(responseCode = "201", description = "Propriedade criada com sucesso")
    public ResponseEntity<EntityModel<PropriedadeResponse>> criar(@RequestBody @Valid PropriedadeRequest request,
                                                                  HttpServletRequest req) {
        PropriedadeResponse response = propriedadeService.criar(request, currentUser(req));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.idPropriedade()).toUri();
        return ResponseEntity.created(location).body(toModel(response, req));
    }

    @GetMapping
    @Operation(summary = "Listar todas as propriedades ativas (ADMIN)")
    public ResponseEntity<CollectionModel<EntityModel<PropriedadeResponse>>> listarTodas(HttpServletRequest req) {
        requireAdmin(req);
        List<EntityModel<PropriedadeResponse>> list = propriedadeService.listarTodas().stream()
                .map(p -> toModel(p, req)).toList();
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(PropriedadeController.class).listarTodas(req)).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar propriedade por ID")
    @ApiResponse(responseCode = "200", description = "Propriedade encontrada")
    @ApiResponse(responseCode = "404", description = "Propriedade não encontrada")
    public ResponseEntity<EntityModel<PropriedadeResponse>> buscarPorId(@PathVariable Long id,
                                                                        HttpServletRequest req) {
        return ResponseEntity.ok(toModel(propriedadeService.buscarPorId(id, currentUser(req)), req));
    }

    @GetMapping("/produtor/{idProdutor}")
    @Operation(summary = "Listar propriedades de um produtor")
    public ResponseEntity<CollectionModel<EntityModel<PropriedadeResponse>>> listarPorProdutor(
            @PathVariable Long idProdutor, HttpServletRequest req) {
        List<EntityModel<PropriedadeResponse>> list = propriedadeService
                .listarPorProdutor(idProdutor, currentUser(req)).stream()
                .map(p -> toModel(p, req)).toList();
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(PropriedadeController.class).listarPorProdutor(idProdutor, req)).withSelfRel()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar propriedade")
    public ResponseEntity<EntityModel<PropriedadeResponse>> atualizar(@PathVariable Long id,
                                                                      @RequestBody @Valid PropriedadeRequest request,
                                                                      HttpServletRequest req) {
        return ResponseEntity.ok(toModel(propriedadeService.atualizar(id, request, currentUser(req)), req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar propriedade — soft delete")
    @ApiResponse(responseCode = "204", description = "Propriedade desativada")
    public ResponseEntity<Void> desativar(@PathVariable Long id, HttpServletRequest req) {
        propriedadeService.desativar(id, currentUser(req));
        return ResponseEntity.noContent().build();
    }

    private EntityModel<PropriedadeResponse> toModel(PropriedadeResponse p, HttpServletRequest req) {
        return EntityModel.of(p,
                linkTo(methodOn(PropriedadeController.class).buscarPorId(p.idPropriedade(), req)).withSelfRel(),
                linkTo(methodOn(PropriedadeController.class).listarPorProdutor(p.idProdutor(), req)).withRel("propriedades-produtor"),
                linkTo(methodOn(TalhaoController.class).listarPorPropriedade(p.idPropriedade(), req)).withRel("talhoes"));
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
