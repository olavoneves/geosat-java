package br.com.geosat.server.controller;

import br.com.geosat.server.dto.request.ErroImagemRequest;
import br.com.geosat.server.dto.request.ImagemSatelitalRequest;
import br.com.geosat.server.dto.request.ProcessarImagemRequest;
import br.com.geosat.server.dto.response.ImagemSatelitalResponse;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.service.ImagemSatelitalService;
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
@RequestMapping("/imagens")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Imagens Satelitais", description = "Registro e processamento de imagens satelitais (NASA/ESA)")
public class ImagemSatelitalController {

    private final ImagemSatelitalService imagemService;

    public ImagemSatelitalController(ImagemSatelitalService imagemService) {
        this.imagemService = imagemService;
    }

    @PostMapping
    @Operation(summary = "Registrar imagem satelital com status PENDENTE")
    @ApiResponse(responseCode = "201", description = "Imagem registrada com sucesso")
    public ResponseEntity<EntityModel<ImagemSatelitalResponse>> criar(@RequestBody @Valid ImagemSatelitalRequest request,
                                                                      HttpServletRequest req) {
        ImagemSatelitalResponse response = imagemService.criar(request, currentUser(req));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.idImagem()).toUri();
        return ResponseEntity.created(location).body(toModel(response, req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar imagem satelital por ID")
    @ApiResponse(responseCode = "200", description = "Imagem encontrada")
    @ApiResponse(responseCode = "404", description = "Imagem não encontrada")
    public ResponseEntity<EntityModel<ImagemSatelitalResponse>> buscarPorId(@PathVariable Long id,
                                                                            HttpServletRequest req) {
        return ResponseEntity.ok(toModel(imagemService.buscarPorId(id, currentUser(req)), req));
    }

    @GetMapping("/talhao/{idTalhao}")
    @Operation(summary = "Listar imagens de um talhão (paginado)")
    public ResponseEntity<Page<ImagemSatelitalResponse>> listarPorTalhao(
            @PathVariable Long idTalhao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest req) {
        return ResponseEntity.ok(imagemService.listarPorTalhao(idTalhao, currentUser(req),
                PageRequest.of(page, size)));
    }

    @PatchMapping("/{id}/processar")
    @Operation(summary = "Atualizar NDVI e marcar imagem como PROCESSADA — trigger pode gerar alerta")
    @ApiResponse(responseCode = "200", description = "Imagem processada com sucesso")
    public ResponseEntity<EntityModel<ImagemSatelitalResponse>> processar(@PathVariable Long id,
                                                                          @RequestBody @Valid ProcessarImagemRequest request,
                                                                          HttpServletRequest req) {
        return ResponseEntity.ok(toModel(imagemService.processar(id, request, currentUser(req)), req));
    }

    @PatchMapping("/{id}/erro")
    @Operation(summary = "Marcar imagem como ERRO com descrição")
    @ApiResponse(responseCode = "200", description = "Imagem marcada com erro")
    public ResponseEntity<EntityModel<ImagemSatelitalResponse>> marcarErro(@PathVariable Long id,
                                                                           @RequestBody @Valid ErroImagemRequest request,
                                                                           HttpServletRequest req) {
        return ResponseEntity.ok(toModel(imagemService.marcarErro(id, request, currentUser(req)), req));
    }

    private EntityModel<ImagemSatelitalResponse> toModel(ImagemSatelitalResponse i, HttpServletRequest req) {
        return EntityModel.of(i,
                linkTo(methodOn(ImagemSatelitalController.class).buscarPorId(i.idImagem(), req)).withSelfRel(),
                linkTo(methodOn(ImagemSatelitalController.class).listarPorTalhao(i.idTalhao(), 0, 20, req)).withRel("imagens-talhao"));
    }

    private UsuarioJava currentUser(HttpServletRequest request) {
        return (UsuarioJava) request.getAttribute("currentUser");
    }
}
