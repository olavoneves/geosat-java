package br.com.geosat.server.controller;

import br.com.geosat.server.dto.request.AlterarSenhaRequest;
import br.com.geosat.server.dto.request.UsuarioRequest;
import br.com.geosat.server.dto.response.UsuarioResponse;
import br.com.geosat.server.exception.UnauthorizedException;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.service.UsuarioService;
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

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema (ADMIN)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários ativos (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<CollectionModel<EntityModel<UsuarioResponse>>> listarTodos(HttpServletRequest req) {
        requireAdmin(req);
        List<EntityModel<UsuarioResponse>> usuarios = usuarioService.listarTodos().stream()
                .map(u -> EntityModel.of(u,
                        linkTo(methodOn(UsuarioController.class).buscarPorId(u.idUsuario(), req)).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).listarTodos(req)).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    public ResponseEntity<EntityModel<UsuarioResponse>> buscarPorId(@PathVariable Long id,
                                                                    HttpServletRequest req) {
        requireAdmin(req);
        UsuarioResponse response = usuarioService.buscarPorId(id);
        EntityModel<UsuarioResponse> model = EntityModel.of(response,
                linkTo(methodOn(UsuarioController.class).buscarPorId(id, req)).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listarTodos(req)).withRel("usuarios"));
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado")
    public ResponseEntity<EntityModel<UsuarioResponse>> atualizar(@PathVariable Long id,
                                                                  @RequestBody @Valid UsuarioRequest request,
                                                                  HttpServletRequest req) {
        requireAdmin(req);
        UsuarioResponse response = usuarioService.atualizar(id, request);
        EntityModel<UsuarioResponse> model = EntityModel.of(response,
                linkTo(methodOn(UsuarioController.class).buscarPorId(id, req)).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @PatchMapping("/{id}/senha")
    @Operation(summary = "Alterar senha do usuário (ADMIN) — revoga todos os tokens")
    @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso")
    public ResponseEntity<Void> alterarSenha(@PathVariable Long id,
                                             @RequestBody @Valid AlterarSenhaRequest request,
                                             HttpServletRequest req) {
        requireAdmin(req);
        usuarioService.alterarSenha(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário (ADMIN) — soft delete")
    @ApiResponse(responseCode = "204", description = "Usuário desativado")
    public ResponseEntity<Void> desativar(@PathVariable Long id, HttpServletRequest req) {
        requireAdmin(req);
        usuarioService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    private void requireAdmin(HttpServletRequest request) {
        UsuarioJava user = (UsuarioJava) request.getAttribute("currentUser");
        if (!"ADMIN".equals(user.getDsRole())) {
            throw new UnauthorizedException("Acesso restrito a administradores");
        }
    }
}
