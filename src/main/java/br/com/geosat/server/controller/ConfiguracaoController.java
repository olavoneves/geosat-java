package br.com.geosat.server.controller;

import br.com.geosat.server.dto.request.ConfiguracaoRequest;
import br.com.geosat.server.dto.response.ConfiguracaoResponse;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.service.ConfiguracaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/configuracoes")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Configurações", description = "Thresholds de alerta por talhão — criados automaticamente pelo trigger")
public class ConfiguracaoController {

    private final ConfiguracaoService configuracaoService;

    public ConfiguracaoController(ConfiguracaoService configuracaoService) {
        this.configuracaoService = configuracaoService;
    }

    @GetMapping("/talhao/{idTalhao}")
    @Operation(summary = "Buscar configuração de thresholds do talhão")
    @ApiResponse(responseCode = "200", description = "Configuração encontrada")
    @ApiResponse(responseCode = "404", description = "Configuração não encontrada (talhão sem configuração)")
    public ResponseEntity<EntityModel<ConfiguracaoResponse>> buscarPorTalhao(@PathVariable Long idTalhao,
                                                                             HttpServletRequest req) {
        ConfiguracaoResponse response = configuracaoService.buscarPorTalhao(idTalhao, currentUser(req));
        return ResponseEntity.ok(toModel(response, req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar thresholds de alerta da configuração")
    @ApiResponse(responseCode = "200", description = "Configuração atualizada")
    public ResponseEntity<EntityModel<ConfiguracaoResponse>> atualizar(@PathVariable Long id,
                                                                       @RequestBody @Valid ConfiguracaoRequest request,
                                                                       HttpServletRequest req) {
        ConfiguracaoResponse response = configuracaoService.atualizar(id, request, currentUser(req));
        return ResponseEntity.ok(toModel(response, req));
    }

    private EntityModel<ConfiguracaoResponse> toModel(ConfiguracaoResponse c, HttpServletRequest req) {
        return EntityModel.of(c,
                linkTo(methodOn(ConfiguracaoController.class).buscarPorTalhao(c.idTalhao(), req)).withSelfRel(),
                linkTo(methodOn(TalhaoController.class).buscarPorId(c.idTalhao(), req)).withRel("talhao"));
    }

    private UsuarioJava currentUser(HttpServletRequest request) {
        return (UsuarioJava) request.getAttribute("currentUser");
    }
}
