# GeoSat API — Java REST Backend

API REST do sistema **GeoSat**, plataforma de monitoramento agrícola que combina imagens satelitais (NASA/ESA) com sensores IoT ESP32 para geração de alertas antecipados de risco para produtores rurais brasileiros.

> **FIAP — Global Solution 2026/1 | Turma 2TDSR**

---

## Tecnologias

| Tecnologia | Versão |
|------------|--------|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Data JPA + Hibernate | via BOM |
| Oracle Database | ojdbc11 |
| Spring HATEOAS | via BOM |
| Spring Validation | via BOM |
| SpringDoc OpenAPI (Swagger) | 2.8.6 |
| spring-security-crypto (BCrypt) | via BOM |
| Lombok | via BOM |
| Maven | 3.x |

---

## Arquitetura

```
br.com.geosat.server
├── config/          # OpenAPI, CORS, Filter registration, AuthProperties
├── controller/      # REST controllers com HATEOAS
├── dto/
│   ├── request/     # Java Records com Bean Validation
│   └── response/    # Java Records com factory from(Entity)
├── exception/       # Exceções customizadas + GlobalExceptionHandler
├── filter/          # AuthTokenFilter (OncePerRequestFilter) + TokenUtils
├── model/           # Entidades JPA mapeando tabelas Oracle TB_GST_*
├── repository/      # Spring Data JPA interfaces
└── service/         # Regras de negócio
```

### Autenticação

Implementada manualmente — **sem Spring Security framework**.

- Login gera `accessToken` (UUID) + `refreshToken` (UUID)
- Apenas o hash SHA-256 de cada token é armazenado no banco
- O plain text é retornado ao cliente e usado no header `Authorization: Bearer <token>`
- `AuthTokenFilter` intercepta todas as requisições protegidas, busca o hash no banco e valida expiração
- Refresh token rotation: ao renovar, o refresh antigo é revogado e dois novos tokens são gerados

---

## Endpoints principais

| Módulo | Base URL | Detalhes |
|--------|----------|----------|
| Autenticação | `/auth` | login, refresh, logout, register |
| Usuários | `/usuarios` | CRUD (ADMIN) |
| Produtores | `/produtores` | CRUD + `/me` |
| Propriedades | `/propriedades` | CRUD + filtro por produtor |
| Talhões | `/talhoes` | CRUD + filtro por propriedade |
| Sensores | `/sensores` | CRUD + filtro por talhão |
| Leituras | `/leituras` | POST + listagem paginada |
| Imagens satelitais | `/imagens` | POST + processar + erro |
| Alertas | `/alertas` | Leitura + visualizar/resolver/reabrir |
| Configurações | `/configuracoes` | GET por talhão + PUT thresholds |

Documentação completa: `GET /swagger-ui.html`

---

## Executando localmente

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Acesso ao banco Oracle GeoSat (com schema já criado)

### Variáveis de ambiente

```bash
export DB_URL=jdbc:oracle:thin:@<host>:<port>/<service>
export DB_USERNAME=<usuario>
export DB_PASSWORD=<senha>
```

No Windows (PowerShell):
```powershell
$env:DB_URL = "jdbc:oracle:thin:@<host>:<port>/<service>"
$env:DB_USERNAME = "<usuario>"
$env:DB_PASSWORD = "<senha>"
```

### Rodando

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Deploy (Railway / Render)

Configure as variáveis de ambiente no painel da plataforma:

| Variável | Valor |
|----------|-------|
| `DB_URL` | `jdbc:oracle:thin:@<host>:<port>/<service>` |
| `DB_USERNAME` | usuário Oracle |
| `DB_PASSWORD` | senha Oracle |

---

## Banco de dados

O schema Oracle **já existe e está populado**. A API usa `ddl-auto=none` — nunca cria nem altera tabelas.

As principais tabelas são: `TB_GST_USUARIO_JAVA`, `TB_GST_PRODUTOR`, `TB_GST_PROPRIEDADE`, `TB_GST_TALHAO`, `TB_GST_SENSOR`, `TB_GST_LEITURA_SENSOR`, `TB_GST_IMAGEM_SATELITAL`, `TB_GST_ALERTA`, `TB_GST_CONFIGURACAO`.

Triggers Oracle são responsáveis por:
- Criar `TB_GST_CONFIGURACAO` ao inserir um talhão
- Gerar alertas ao inserir leituras com umidade abaixo do threshold
- Gerar alertas ao processar imagem com NDVI abaixo do threshold
- Registrar logs em `TB_GST_LOG_ALERTA` ao mudar status de alerta

---

## Fluxo de uso básico

```
1. POST /auth/login           → obter accessToken + refreshToken
2. POST /produtores           → cadastrar produtor (vincula ao usuário logado)
3. POST /propriedades         → cadastrar propriedade
4. POST /talhoes              → cadastrar talhão (trigger cria configuração)
5. POST /sensores             → cadastrar sensor ESP32
6. POST /leituras             → registrar leitura (trigger pode gerar alerta)
7. GET  /alertas/produtor/me/pendentes → consultar alertas pendentes
8. PATCH /alertas/{id}/resolver        → resolver alerta
```

---

## Contexto do sistema GeoSat

O GeoSat é composto por três módulos integrados:

- **Esta API Java** — core da plataforma, consumida pelo app mobile React Native dos produtores rurais
- **API .NET** — painel administrativo para cooperativas e gestores, responsável por processar imagens satelitais
- **ESP32** — sensores IoT instalados nos talhões que enviam leituras periodicamente

---

*Última atualização: 2026-05-27*
