package br.com.geosat.server.dto.request;

import jakarta.validation.constraints.*;

public record ProdutorRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        String nmNome,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter 11 dígitos numéricos")
        String nrCpf,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150)
        String dsEmail,

        @Size(max = 15)
        String nrTelefone,

        @Size(max = 255)
        String dsFcmToken
) {}
