package br.com.geosat.server.service;

import br.com.geosat.server.dto.request.AlterarSenhaRequest;
import br.com.geosat.server.dto.request.UsuarioRequest;
import br.com.geosat.server.dto.response.UsuarioResponse;
import br.com.geosat.server.exception.BusinessException;
import br.com.geosat.server.exception.ResourceNotFoundException;
import br.com.geosat.server.model.UsuarioJava;
import br.com.geosat.server.repository.AccessTokenJavaRepository;
import br.com.geosat.server.repository.RefreshTokenJavaRepository;
import br.com.geosat.server.repository.UsuarioJavaRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioJavaRepository usuarioRepo;
    private final AccessTokenJavaRepository accessTokenRepo;
    private final RefreshTokenJavaRepository refreshTokenRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioJavaRepository usuarioRepo,
                          AccessTokenJavaRepository accessTokenRepo,
                          RefreshTokenJavaRepository refreshTokenRepo) {
        this.usuarioRepo = usuarioRepo;
        this.accessTokenRepo = accessTokenRepo;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepo.findAllByFlAtivo("S").stream()
                .map(UsuarioResponse::from)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.from(getAtivo(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        UsuarioJava usuario = getAtivo(id);

        if (!usuario.getDsEmail().equals(request.dsEmail())
                && usuarioRepo.existsByDsEmail(request.dsEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        usuario.setNmNome(request.nmNome());
        usuario.setDsEmail(request.dsEmail());
        if (request.dsRole() != null) {
            usuario.setDsRole(request.dsRole());
        }

        return UsuarioResponse.from(usuarioRepo.save(usuario));
    }

    @Transactional
    public void alterarSenha(Long id, AlterarSenhaRequest request) {
        UsuarioJava usuario = getAtivo(id);
        usuario.setDsSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuarioRepo.save(usuario);

        accessTokenRepo.revokeAllActiveByUsuarioId(id);
        refreshTokenRepo.revokeAllActiveByUsuarioId(id);
    }

    @Transactional
    public void desativar(Long id) {
        UsuarioJava usuario = getAtivo(id);
        usuario.setFlAtivo("N");
        usuarioRepo.save(usuario);

        accessTokenRepo.revokeAllActiveByUsuarioId(id);
        refreshTokenRepo.revokeAllActiveByUsuarioId(id);
    }

    private UsuarioJava getAtivo(Long id) {
        return usuarioRepo.findByIdUsuarioAndFlAtivo(id, "S")
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }
}
