package br.com.geosat.server.controller;

import br.com.geosat.server.dto.request.LoginRequest;
import br.com.geosat.server.dto.request.RefreshTokenRequest;
import br.com.geosat.server.dto.request.UsuarioRequest;
import br.com.geosat.server.dto.response.TokenResponse;
import br.com.geosat.server.dto.response.UsuarioResponse;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login, renovação e revogação de tokens")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e obter tokens")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token via refresh token")
    @ApiResponse(responseCode = "200", description = "Tokens renovados com sucesso")
    @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Revogar todos os tokens do usuário logado")
    @ApiResponse(responseCode = "204", description = "Logout realizado com sucesso")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        UsuarioJava usuario = (UsuarioJava) httpRequest.getAttribute("currentUser");
        authService.logout(usuario);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Registrar novo usuário (ADMIN)")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso restrito a administradores")
    public ResponseEntity<UsuarioResponse> register(@RequestBody @Valid UsuarioRequest request,
                                                    HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        UsuarioResponse response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    private void requireAdmin(HttpServletRequest request) {
        UsuarioJava user = (UsuarioJava) request.getAttribute("currentUser");
        if (!"ADMIN".equals(user.getDsRole())) {
            throw new br.com.geosat.server.exception.UnauthorizedException("Acesso restrito a administradores");
        }
    }
}
