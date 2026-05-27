package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.*;

public record UsuarioRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nmNome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150)
        String dsEmail,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
        String dsSenha,

        @Pattern(regexp = "^(ADMIN|USER)$", message = "Role deve ser ADMIN ou USER")
        String dsRole
) {}
