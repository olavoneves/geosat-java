package br.com.geosat.server.controller;

import br.com.geosat.server.dto.request.TalhaoRequest;
import br.com.geosat.server.dto.response.TalhaoResponse;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.service.TalhaoService;
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
@RequestMapping("/talhoes")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Talhões", description = "Gerenciamento de talhões dentro de propriedades")
public class TalhaoController {

    private final TalhaoService talhaoService;

    public TalhaoController(TalhaoService talhaoService) {
        this.talhaoService = talhaoService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar talhão — trigger cria Configuracao automaticamente")
    @ApiResponse(responseCode = "201", description = "Talhão criado com sucesso")
    public ResponseEntity<EntityModel<TalhaoResponse>> criar(@RequestBody @Valid TalhaoRequest request,
                                                             HttpServletRequest req) {
        TalhaoResponse response = talhaoService.criar(request, currentUser(req));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.idTalhao()).toUri();
        return ResponseEntity.created(location).body(toModel(response, req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar talhão por ID")
    @ApiResponse(responseCode = "200", description = "Talhão encontrado")
    @ApiResponse(responseCode = "404", description = "Talhão não encontrado")
    public ResponseEntity<EntityModel<TalhaoResponse>> buscarPorId(@PathVariable Long id,
                                                                   HttpServletRequest req) {
        return ResponseEntity.ok(toModel(talhaoService.buscarPorId(id, currentUser(req)), req));
    }

    @GetMapping("/propriedade/{idPropriedade}")
    @Operation(summary = "Listar talhões de uma propriedade")
    public ResponseEntity<CollectionModel<EntityModel<TalhaoResponse>>> listarPorPropriedade(
            @PathVariable Long idPropriedade, HttpServletRequest req) {
        List<EntityModel<TalhaoResponse>> list = talhaoService
                .listarPorPropriedade(idPropriedade, currentUser(req)).stream()
                .map(t -> toModel(t, req)).toList();
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(TalhaoController.class).listarPorPropriedade(idPropriedade, req)).withSelfRel()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar talhão")
    public ResponseEntity<EntityModel<TalhaoResponse>> atualizar(@PathVariable Long id,
                                                                 @RequestBody @Valid TalhaoRequest request,
                                                                 HttpServletRequest req) {
        return ResponseEntity.ok(toModel(talhaoService.atualizar(id, request, currentUser(req)), req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar talhão — soft delete")
    @ApiResponse(responseCode = "204", description = "Talhão desativado")
    public ResponseEntity<Void> desativar(@PathVariable Long id, HttpServletRequest req) {
        talhaoService.desativar(id, currentUser(req));
        return ResponseEntity.noContent().build();
    }

    private EntityModel<TalhaoResponse> toModel(TalhaoResponse t, HttpServletRequest req) {
        return EntityModel.of(t,
                linkTo(methodOn(TalhaoController.class).buscarPorId(t.idTalhao(), req)).withSelfRel(),
                linkTo(methodOn(TalhaoController.class).listarPorPropriedade(t.idPropriedade(), req)).withRel("talhoes-propriedade"),
                linkTo(methodOn(SensorController.class).listarPorTalhao(t.idTalhao(), req)).withRel("sensores"),
                linkTo(methodOn(ConfiguracaoController.class).buscarPorTalhao(t.idTalhao(), req)).withRel("configuracao"));
    }

    private UsuarioJava currentUser(HttpServletRequest request) {
        return (UsuarioJava) request.getAttribute("currentUser");
    }
}
