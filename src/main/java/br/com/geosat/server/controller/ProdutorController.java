package br.com.geosat.server.controller;

import br.com.geosat.server.dto.request.ProdutorRequest;
import br.com.geosat.server.dto.response.ProdutorResponse;
import br.com.geosat.server.exception.ForbiddenException;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.service.ProdutorService;
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
@RequestMapping("/produtores")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Produtores", description = "Gerenciamento de produtores rurais")
public class ProdutorController {

    private final ProdutorService produtorService;

    public ProdutorController(ProdutorService produtorService) {
        this.produtorService = produtorService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar produtor vinculado ao usuário logado")
    @ApiResponse(responseCode = "201", description = "Produtor criado com sucesso")
    public ResponseEntity<EntityModel<ProdutorResponse>> criar(@RequestBody @Valid ProdutorRequest request,
                                                               HttpServletRequest req) {
        UsuarioJava usuario = currentUser(req);
        ProdutorResponse response = produtorService.criar(request, usuario);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.idProdutor()).toUri();
        return ResponseEntity.created(location).body(toModel(response, req));
    }

    @GetMapping
    @Operation(summary = "Listar todos os produtores ativos (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<CollectionModel<EntityModel<ProdutorResponse>>> listarTodos(HttpServletRequest req) {
        requireAdmin(req);
        List<EntityModel<ProdutorResponse>> list = produtorService.listarTodos().stream()
                .map(p -> toModel(p, req)).toList();
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(ProdutorController.class).listarTodos(req)).withSelfRel()));
    }

    @GetMapping("/me")
    @Operation(summary = "Buscar dados do produtor do usuário logado")
    @ApiResponse(responseCode = "200", description = "Produtor encontrado")
    public ResponseEntity<EntityModel<ProdutorResponse>> buscarMeu(HttpServletRequest req) {
        UsuarioJava usuario = currentUser(req);
        return ResponseEntity.ok(toModel(produtorService.buscarMeu(usuario), req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produtor por ID")
    @ApiResponse(responseCode = "200", description = "Produtor encontrado")
    @ApiResponse(responseCode = "404", description = "Produtor não encontrado")
    public ResponseEntity<EntityModel<ProdutorResponse>> buscarPorId(@PathVariable Long id,
                                                                     HttpServletRequest req) {
        return ResponseEntity.ok(toModel(produtorService.buscarPorId(id, currentUser(req)), req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do produtor")
    @ApiResponse(responseCode = "200", description = "Produtor atualizado")
    public ResponseEntity<EntityModel<ProdutorResponse>> atualizar(@PathVariable Long id,
                                                                   @RequestBody @Valid ProdutorRequest request,
                                                                   HttpServletRequest req) {
        return ResponseEntity.ok(toModel(produtorService.atualizar(id, request, currentUser(req)), req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar produtor (ADMIN) — soft delete")
    @ApiResponse(responseCode = "204", description = "Produtor desativado")
    public ResponseEntity<Void> desativar(@PathVariable Long id, HttpServletRequest req) {
        requireAdmin(req);
        produtorService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<ProdutorResponse> toModel(ProdutorResponse p, HttpServletRequest req) {
        return EntityModel.of(p,
                linkTo(methodOn(ProdutorController.class).buscarPorId(p.idProdutor(), req)).withSelfRel(),
                linkTo(methodOn(ProdutorController.class).listarTodos(req)).withRel("produtores"),
                linkTo(methodOn(PropriedadeController.class)
                        .listarPorProdutor(p.idProdutor(), req)).withRel("propriedades"));
    }

    private UsuarioJava currentUser(HttpServletRequest request) {
        return (UsuarioJava) request.getAttribute("currentUser");
    }

    private void requireAdmin(HttpServletRequest request) {
        UsuarioJava user = currentUser(request);
        if (!"ADMIN".equals(user.getDsRole())) {
            throw new ForbiddenException("Acesso restrito a administradores");
        }
    }
}
