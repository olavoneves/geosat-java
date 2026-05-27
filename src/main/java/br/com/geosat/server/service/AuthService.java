package br.com.geosat.server.service;

import br.com.geosat.server.config.AuthProperties;
import br.com.geosat.server.dto.request.LoginRequest;
import br.com.geosat.server.dto.request.RefreshTokenRequest;
import br.com.geosat.server.dto.request.UsuarioRequest;
import br.com.geosat.server.dto.response.TokenResponse;
import br.com.geosat.server.dto.response.UsuarioResponse;
import br.com.geosat.server.exception.BusinessException;
import br.com.geosat.server.exception.ResourceNotFoundException;
import br.com.geosat.server.exception.TokenExpiredException;
import br.com.geosat.server.exception.UnauthorizedException;
import br.com.geosat.server.filter.TokenUtils;
import br.com.geosat.server.model.AccessTokenJava;
import br.com.geosat.server.model.RefreshTokenJava;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.repository.AccessTokenJavaRepository;
import br.com.geosat.server.repository.RefreshTokenJavaRepository;
import br.com.geosat.server.repository.UsuarioJavaRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioJavaRepository usuarioRepo;
    private final AccessTokenJavaRepository accessTokenRepo;
    private final RefreshTokenJavaRepository refreshTokenRepo;
    private final AuthProperties authProperties;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UsuarioJavaRepository usuarioRepo,
                       AccessTokenJavaRepository accessTokenRepo,
                       RefreshTokenJavaRepository refreshTokenRepo,
                       AuthProperties authProperties) {
        this.usuarioRepo = usuarioRepo;
        this.accessTokenRepo = accessTokenRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.authProperties = authProperties;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        UsuarioJava usuario = usuarioRepo.findByDsEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!"S".equals(usuario.getFlAtivo())) {
            throw new UnauthorizedException("Usuário inativo");
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getDsSenhaHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        return generateTokenPair(usuario);
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        String hash = TokenUtils.hashToken(request.refreshToken());

        RefreshTokenJava refreshToken = refreshTokenRepo.findByDsToken(hash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        if ("S".equals(refreshToken.getFlRevogado())) {
            throw new UnauthorizedException("Refresh token revogado");
        }

        if (refreshToken.getDtExpiracao().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        refreshToken.setFlRevogado("S");
        refreshTokenRepo.save(refreshToken);

        return generateTokenPair(refreshToken.getUsuario());
    }

    @Transactional
    public void logout(UsuarioJava usuario) {
        accessTokenRepo.revokeAllActiveByUsuarioId(usuario.getIdUsuario());
        refreshTokenRepo.revokeAllActiveByUsuarioId(usuario.getIdUsuario());
    }

    @Transactional
    public UsuarioResponse register(UsuarioRequest request) {
        if (usuarioRepo.existsByDsEmail(request.dsEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        UsuarioJava usuario = new UsuarioJava();
        usuario.setNmNome(request.nmNome());
        usuario.setDsEmail(request.dsEmail());
        usuario.setDsSenhaHash(passwordEncoder.encode(request.dsSenha()));
        usuario.setDsRole(request.dsRole() != null ? request.dsRole() : "USER");

        return UsuarioResponse.from(usuarioRepo.save(usuario));
    }

    private TokenResponse generateTokenPair(UsuarioJava usuario) {
        String rawAccess = UUID.randomUUID().toString();
        String rawRefresh = UUID.randomUUID().toString();

        AccessTokenJava accessToken = new AccessTokenJava();
        accessToken.setUsuario(usuario);
        accessToken.setDsToken(TokenUtils.hashToken(rawAccess));
        accessToken.setDtExpiracao(LocalDateTime.now()
                .plusMinutes(authProperties.getAccessTokenExpirationMinutes()));
        accessTokenRepo.save(accessToken);

        RefreshTokenJava refreshToken = new RefreshTokenJava();
        refreshToken.setUsuario(usuario);
        refreshToken.setDsToken(TokenUtils.hashToken(rawRefresh));
        refreshToken.setDtExpiracao(LocalDateTime.now()
                .plusDays(authProperties.getRefreshTokenExpirationDays()));
        refreshTokenRepo.save(refreshToken);

        long expiresIn = (long) authProperties.getAccessTokenExpirationMinutes() * 60;
        return new TokenResponse(rawAccess, rawRefresh, expiresIn, usuario.getDsRole());
    }
}
