package br.com.geosat.server.filter;

import br.com.geosat.server.dto.response.ErrorResponse;
import br.com.geosat.server.exception.TokenExpiredException;
import br.com.geosat.server.model.AccessTokenJava;
import br.com.geosat.server.repository.AccessTokenJavaRepository;
import br.com.geosat.server.repository.UsuarioJavaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/auth/login", "/auth/refresh",
            "/swagger-ui", "/v3/api-docs",
            "/actuator/health"
    );

    private final AccessTokenJavaRepository accessTokenRepo;
    private final UsuarioJavaRepository usuarioRepo;
    private final ObjectMapper objectMapper;

    public AuthTokenFilter(AccessTokenJavaRepository accessTokenRepo,
                           UsuarioJavaRepository usuarioRepo) {
        this.accessTokenRepo = accessTokenRepo;
        this.usuarioRepo = usuarioRepo;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, request.getRequestURI(), "Token não fornecido");
            return;
        }

        String rawToken = authHeader.substring(7);
        String tokenHash = TokenUtils.hashToken(rawToken);

        Optional<AccessTokenJava> tokenOpt = accessTokenRepo.findByDsToken(tokenHash);

        if (tokenOpt.isEmpty()) {
            sendUnauthorized(response, request.getRequestURI(), "Token inválido");
            return;
        }

        AccessTokenJava accessToken = tokenOpt.get();

        if ("S".equals(accessToken.getFlRevogado())) {
            sendUnauthorized(response, request.getRequestURI(), "Token revogado");
            return;
        }

        if (accessToken.getDtExpiracao().isBefore(LocalDateTime.now())) {
            sendUnauthorized(response, request.getRequestURI(), "Token expirado");
            return;
        }

        request.setAttribute("currentUser", accessToken.getUsuario());
        chain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String path, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse error = ErrorResponse.of(401, "Unauthorized", message, path);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
