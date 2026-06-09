# GeoSat API ÔÇö Java REST Backend

API REST do sistema **GeoSat**, plataforma de monitoramento agr├¡cola que combina imagens satelitais (NASA/ESA) com sensores IoT ESP32 para gera├º├úo de alertas antecipados de risco para produtores rurais brasileiros.

> **FIAP ÔÇö Global Solution 2026/1 | 2TDS Fevereiro**

> **Professor Orientador:** Marcel Stefan Wagner

---

## ­ƒöù Links

| Recurso | Link |
|---------|------|
| ­ƒÜÇ Deploy (produ├º├úo) | *https://geosat-java.onrender.com* |
| ­ƒôû Swagger UI (produ├º├úo) | *https://geosat-java.onrender.com/swagger-ui/index.html* |
| ­ƒÄÑ V├¡deo de Apresenta├º├úo | **[SUBSTITUIR ÔÇö link do YouTube ap├│s grava├º├úo]** |
| ­ƒÄ» V├¡deo Pitch (3 min) | **[SUBSTITUIR ÔÇö link do YouTube ap├│s grava├º├úo]** |
| ­ƒÆ╗ Reposit├│rio GitHub | *https://github.com/olavoneves/geosat-java* |

---

## ­ƒôÉ Diagrama de Entidades

```mermaid
erDiagram
    TB_GST_USUARIO_JAVA {
        NUMBER id_usuario PK
        VARCHAR2 nm_nome
        VARCHAR2 ds_email UK
        VARCHAR2 ds_senha_hash
        VARCHAR2 ds_role
        CHAR fl_ativo
        TIMESTAMP dt_criacao
    }

    TB_GST_ACCESS_TOKEN_JAVA {
        NUMBER id_access PK
        NUMBER id_usuario FK
        VARCHAR2 ds_token UK
        TIMESTAMP dt_expiracao
        CHAR fl_revogado
        TIMESTAMP dt_criacao
    }

    TB_GST_REFRESH_TOKEN_JAVA {
        NUMBER id_refresh PK
        NUMBER id_usuario FK
        VARCHAR2 ds_token UK
        TIMESTAMP dt_expiracao
        CHAR fl_revogado
        TIMESTAMP dt_criacao
    }

    TB_GST_PRODUTOR {
        NUMBER id_produtor PK
        NUMBER id_usuario FK
        VARCHAR2 nm_nome
        CHAR nr_cpf UK
        VARCHAR2 ds_email UK
        VARCHAR2 nr_telefone
        VARCHAR2 ds_fcm_token
        CHAR fl_ativo
        TIMESTAMP dt_criacao
    }

    TB_GST_PROPRIEDADE {
        NUMBER id_propriedade PK
        NUMBER id_produtor FK
        VARCHAR2 nm_nome
        VARCHAR2 nm_municipio
        CHAR sg_estado
        NUMBER nr_area_ha
        CHAR fl_ativa
        TIMESTAMP dt_criacao
    }

    TB_GST_TALHAO {
        NUMBER id_talhao PK
        NUMBER id_propriedade FK
        VARCHAR2 nm_nome
        VARCHAR2 ds_cultura
        NUMBER nr_area_ha
        CHAR fl_ativo
        TIMESTAMP dt_criacao
    }

    TB_GST_SENSOR {
        NUMBER id_sensor PK
        NUMBER id_talhao FK
        VARCHAR2 cd_identificador_hw UK
        VARCHAR2 ds_localizacao
        CHAR fl_ativo
        TIMESTAMP dt_instalacao
    }

    TB_GST_LEITURA_SENSOR {
        NUMBER id_leitura PK
        NUMBER id_sensor FK
        TIMESTAMP dt_leitura
        NUMBER nr_temp_ar
        NUMBER nr_umidade_solo
        NUMBER nr_luminosidade
        CHAR fl_transmitida
        TIMESTAMP dt_recebida
    }

    TB_GST_IMAGEM_SATELITAL {
        NUMBER id_imagem PK
        NUMBER id_talhao FK
        DATE dt_captura
        NUMBER nr_ndvi
        VARCHAR2 ds_fonte
        VARCHAR2 ds_status_proc
        VARCHAR2 ds_erro
        TIMESTAMP dt_processado
    }

    TB_GST_CONFIGURACAO {
        NUMBER id_config PK
        NUMBER id_talhao FK
        NUMBER nr_threshold_umid_min
        NUMBER nr_threshold_ndvi_min
        NUMBER nr_janela_fusao_horas
        TIMESTAMP dt_atualizacao
    }

    TB_GST_ALERTA {
        NUMBER id_alerta PK
        NUMBER id_talhao FK
        VARCHAR2 tp_tipo
        VARCHAR2 tp_nivel
        VARCHAR2 tp_origem
        VARCHAR2 ds_descricao
        VARCHAR2 st_status
        TIMESTAMP dt_gerado
        TIMESTAMP dt_visualizado
        TIMESTAMP dt_resolvido
    }

    TB_GST_LOG_ALERTA {
        NUMBER id_log PK
        NUMBER id_alerta FK
        VARCHAR2 ds_acao
        VARCHAR2 ds_origem
        VARCHAR2 ds_observacao
        TIMESTAMP dt_evento
    }

    TB_GST_USUARIO_JAVA ||--o{ TB_GST_ACCESS_TOKEN_JAVA : "possui"
    TB_GST_USUARIO_JAVA ||--o{ TB_GST_REFRESH_TOKEN_JAVA : "possui"
    TB_GST_USUARIO_JAVA ||--o{ TB_GST_PRODUTOR : "cadastra"
    TB_GST_PRODUTOR     ||--o{ TB_GST_PROPRIEDADE : "possui"
    TB_GST_PROPRIEDADE  ||--o{ TB_GST_TALHAO : "subdivide"
    TB_GST_TALHAO       ||--o{ TB_GST_SENSOR : "recebe"
    TB_GST_TALHAO       ||--o{ TB_GST_IMAGEM_SATELITAL : "associa"
    TB_GST_TALHAO       ||--o{ TB_GST_ALERTA : "dispara"
    TB_GST_TALHAO       ||--|| TB_GST_CONFIGURACAO : "configura"
    TB_GST_SENSOR       ||--o{ TB_GST_LEITURA_SENSOR : "gera"
    TB_GST_ALERTA       ||--o{ TB_GST_LOG_ALERTA : "registra"
```

---

## ­ƒôè Diagrama UML ÔÇö Arquitetura de Classes

```mermaid
classDiagram
    class AuthController {
        +login(LoginRequest) TokenResponse
        +refresh(RefreshTokenRequest) TokenResponse
        +logout(HttpServletRequest) void
        +register(UsuarioRequest) UsuarioResponse
    }

    class AuthService {
        -usuarioRepo UsuarioJavaRepository
        -accessTokenRepo AccessTokenJavaRepository
        -refreshTokenRepo RefreshTokenJavaRepository
        -passwordEncoder BCryptPasswordEncoder
        +login(String email, String senha) TokenResponse
        +refresh(String refreshToken) TokenResponse
        +logout(String accessToken) void
        +register(UsuarioRequest) UsuarioResponse
        -hashToken(String token) String
        -gerarAccessToken(UsuarioJava) String
        -gerarRefreshToken(UsuarioJava) String
    }

    class AuthTokenFilter {
        -usuarioRepo UsuarioJavaRepository
        -accessTokenRepo AccessTokenJavaRepository
        +doFilterInternal(request, response, chain) void
        -extrairToken(request) String
        -hashToken(String token) String
    }

    class ProdutorController {
        +criar(ProdutorRequest, request) EntityModel
        +buscarPorId(Long id) EntityModel
        +buscarMeu(request) EntityModel
        +listarTodos() CollectionModel
        +atualizar(Long id, ProdutorRequest) EntityModel
        +desativar(Long id) void
    }

    class ProdutorService {
        -produtorRepo ProdutorRepository
        +criar(ProdutorRequest, UsuarioJava) ProdutorResponse
        +buscarPorId(Long id) ProdutorResponse
        +buscarPorUsuario(UsuarioJava) ProdutorResponse
        +listarTodos() List~ProdutorResponse~
        +atualizar(Long id, ProdutorRequest) ProdutorResponse
        +desativar(Long id) void
    }

    class GlobalExceptionHandler {
        +handleNotFound(ResourceNotFoundException) ErrorResponse
        +handleBusiness(BusinessException) ErrorResponse
        +handleUnauthorized(UnauthorizedException) ErrorResponse
        +handleForbidden(ForbiddenException) ErrorResponse
        +handleValidation(MethodArgumentNotValidException) ErrorResponse
        +handleConflict(DataIntegrityViolationException) ErrorResponse
        +handleGeneric(Exception) ErrorResponse
    }

    class UsuarioJava {
        -Long idUsuario
        -String nmNome
        -String dsEmail
        -String dsSenhaHash
        -String dsRole
        -Auditoria auditoria
        +getFlAtivo() String
        +getDtCriacao() LocalDateTime
    }

    class Auditoria {
        -String flAtivo
        -LocalDateTime dtCriacao
        +prePersist() void
    }

    class Produtor {
        -Long idProdutor
        -UsuarioJava usuario
        -String nmNome
        -String nrCpf
        -String dsEmail
        -Auditoria auditoria
    }

    class Talhao {
        -Long idTalhao
        -Propriedade propriedade
        -String nmNome
        -String dsCultura
        -Number nrAreaHa
        -Auditoria auditoria
    }

    class Alerta {
        -Long idAlerta
        -Talhao talhao
        -String tpTipo
        -String tpNivel
        -String tpOrigem
        -String stStatus
        -LocalDateTime dtGerado
    }

    AuthController --> AuthService
    ProdutorController --> ProdutorService
    AuthTokenFilter --> UsuarioJavaRepository
    UsuarioJava --> Auditoria
    Produtor --> Auditoria
    Talhao --> Auditoria
    Produtor --> UsuarioJava
    Talhao --> Propriedade
    Alerta --> Talhao
```

---

## ­ƒº¬ Evid├¬ncias de Testes

### Testes via Swagger UI

#### Login e Autentica├º├úo
> ­ƒô© *[Substituir por print do POST /auth/login com resposta 200 e tokens]*

#### Cadastro de Produtor
> ­ƒô© *[Substituir por print do POST /produtores com resposta 201 e HATEOAS links]*

#### Leitura de Sensor e Alerta Autom├ítico
> ­ƒô© *[Substituir por print do POST /leituras com resposta 201]*
> ­ƒô© *[Substituir por print do GET /alertas/produtor/me/pendentes mostrando alerta gerado automaticamente]*

#### Valida├º├úo de Entrada (400)
> ­ƒô© *[Substituir por print de requisi├º├úo inv├ílida retornando 400 com fieldErrors]*

#### Controle de Acesso (403)
> ­ƒô© *[Substituir por print de USER tentando endpoint ADMIN retornando 403]*

---

### Persist├¬ncia no Banco Oracle

#### Dados inseridos via API
> ­ƒô© *[Substituir por print do SQL Developer ou similar mostrando SELECT em TB_GST_PRODUTOR]*
> ­ƒô© *[Substituir por print mostrando SELECT em TB_GST_ALERTA com alertas gerados pelo trigger]*

#### Configura├º├úo criada automaticamente pelo trigger
> ­ƒô© *[Substituir por print mostrando SELECT em TB_GST_CONFIGURACAO ap├│s INSERT em TB_GST_TALHAO]*

#### Log de auditoria de alerta
> ­ƒô© *[Substituir por print mostrando SELECT em TB_GST_LOG_ALERTA com hist├│rico de status]*

---

## ­ƒÅù´©Å Arquitetura

```
br.com.geosat.server
Ôö£ÔöÇÔöÇ config/          # OpenAPI, CORS, Filter registration, AuthProperties
Ôö£ÔöÇÔöÇ controller/      # REST controllers com HATEOAS e Swagger annotations
Ôö£ÔöÇÔöÇ dto/
Ôöé   Ôö£ÔöÇÔöÇ request/     # Java Records com Bean Validation (@NotBlank, @Email, @Pattern...)
Ôöé   ÔööÔöÇÔöÇ response/    # Java Records com factory from(Entity)
Ôö£ÔöÇÔöÇ exception/       # Exce├º├Áes customizadas + GlobalExceptionHandler (@RestControllerAdvice)
Ôö£ÔöÇÔöÇ filter/          # AuthTokenFilter (OncePerRequestFilter) + TokenUtils (SHA-256)
Ôö£ÔöÇÔöÇ model/           # Entidades JPA com @Embedded Auditoria (modelagem avan├ºada)
Ôö£ÔöÇÔöÇ repository/      # Spring Data JPA ÔÇö JpaRepository
ÔööÔöÇÔöÇ service/         # Regras de neg├│cio e orquestra├º├úo
```

### Modelagem Avan├ºada ÔÇö @Embedded

A classe `Auditoria` ├® um `@Embeddable` que encapsula os campos `dtCriacao` e `flAtivo` presentes em m├║ltiplas entidades. Usada com `@Embedded` em `UsuarioJava`, `Produtor`, `Propriedade`, `Talhao` e `Sensor`, demonstrando modelagem avan├ºada com reutiliza├º├úo de componentes JPA.

### Autentica├º├úo Manual (sem Spring Security)

- Login gera `accessToken` (UUID) + `refreshToken` (UUID)
- Apenas o **hash SHA-256** de cada token ├® armazenado no banco
- O plain text ├® retornado ao cliente: `Authorization: Bearer <token>`
- `AuthTokenFilter` intercepta requisi├º├Áes protegidas, busca hash no banco, valida expira├º├úo
- **Refresh token rotation**: ao renovar, o refresh antigo ├® revogado e dois novos tokens s├úo gerados

---

## ­ƒøá´©Å Tecnologias

| Tecnologia | Vers├úo |
|------------|--------|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Data JPA + Hibernate | via BOM |
| Spring HATEOAS | via BOM |
| Spring Validation | via BOM |
| Spring Boot DevTools | via BOM |
| Oracle Database (ojdbc11) | via BOM |
| spring-security-crypto (BCrypt) | via BOM |
| SpringDoc OpenAPI / Swagger UI | 2.8.6 |
| Lombok | via BOM |
| Maven | 3.x |

---

## ­ƒôï Endpoints

| M├│dulo | Base URL | Opera├º├Áes |
|--------|----------|-----------|
| Autentica├º├úo | `/auth` | login, refresh, logout, register (ADMIN) |
| Usu├írios | `/usuarios` | GET, PUT, DELETE (ADMIN) |
| Produtores | `/produtores` | POST, GET, GET/me, PUT, DELETE |
| Propriedades | `/propriedades` | POST, GET, GET/produtor/{id}, PUT, DELETE |
| Talh├Áes | `/talhoes` | POST, GET, GET/propriedade/{id}, PUT, DELETE |
| Sensores | `/sensores` | POST, GET, GET/talhao/{id}, PUT, DELETE |
| Leituras | `/leituras` | POST, GET/{id}, GET/sensor/{id}, GET/sensor/{id}/last |
| Imagens Satelitais | `/imagens` | POST, GET, PATCH/processar, PATCH/erro |
| Alertas | `/alertas` | GET, GET/{id}, GET/talhao, GET/produtor/me, PATCH/visualizar, PATCH/resolver, PATCH/reabrir |
| Configura├º├Áes | `/configuracoes` | GET/talhao/{id}, PUT/{id} |

Documenta├º├úo completa interativa: `GET /swagger-ui.html`

---

## ÔÜÖ´©Å Executando Localmente

### Pr├®-requisitos

- Java 21+
- Maven 3.8+
- Acesso ao banco Oracle GeoSat (schema j├í criado e populado)

### Vari├íveis de Ambiente

Crie um arquivo `.env.local` na raiz do projeto (n├úo commitar):

```bash
DB_GST_URL=jdbc:oracle:thin:@<host>:<port>/<service>
DB_GST_USERNAME=<seu_rm>
DB_GST_PASSWORD=<sua_senha>
```

Ou exporte diretamente:

```bash
# Linux/macOS
export DB_GST_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
export DB_GST_USERNAME=SEU_RM
export DB_GST_PASSWORD=SUA_SENHA

# Windows (PowerShell)
$env:DB_GST_URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL"
$env:DB_GST_USERNAME = "SEU_RM"
$env:DB_GST_PASSWORD = "SUA_SENHA"
```

### Rodando

```bash
./mvnw spring-boot:run
```

Acesse:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`

---

## Ôÿü´©Å Deploy (Render)

1. Fa├ºa push do reposit├│rio para o GitHub
2. No Render, crie um novo **Web Service** apontando para o reposit├│rio
3. Configure o Build Command: `./mvnw package -DskipTests`
4. Configure o Start Command: `java -jar target/*.jar`
5. Em **Environment Variables**, adicione:

| Vari├ível | Valor |
|----------|-------|
| `DB_GST_URL` | `jdbc:oracle:thin:@<host>:<port>/<service>` |
| `DB_GST_USERNAME` | seu RM Oracle |
| `DB_GST_PASSWORD` | sua senha Oracle |

Ou importe o arquivo `.env` diretamente pelo painel do Render.

---

## ­ƒº¬ Exemplos de Teste

### 1. Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "master@geosat.com", "senha": "master123"}'
```

Resposta:
```json
{
  "accessToken": "uuid-gerado",
  "refreshToken": "uuid-gerado",
  "expiresIn": 1800,
  "role": "ADMIN"
}
```

### 2. Criar Produtor (autenticado)

```bash
curl -X POST http://localhost:8080/produtores \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "nmNome": "Jo├úo Silva",
    "nrCpf": "12345678901",
    "dsEmail": "joao@fazenda.com",
    "nrTelefone": "11999999999"
  }'
```

### 3. Registrar Leitura de Sensor

```bash
curl -X POST http://localhost:8080/leituras \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "idSensor": 1,
    "dtLeitura": "2026-06-01T10:00:00",
    "nrTempAr": 28.5,
    "nrUmidadeSolo": 25.0,
    "nrLuminosidade": 850.0
  }'
```

> O trigger Oracle verifica automaticamente se `nrUmidadeSolo` est├í abaixo do threshold configurado para o talh├úo. Se estiver, um alerta ├® gerado sem a├º├úo adicional da API.

### 4. Consultar Alertas Pendentes

```bash
curl -X GET http://localhost:8080/alertas/produtor/me/pendentes \
  -H "Authorization: Bearer <accessToken>"
```

### 5. Renovar Token

```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "<refreshToken>"}'
```

---

## ­ƒùä´©Å Banco de Dados

O schema Oracle **j├í existe e est├í populado**. A API usa `ddl-auto=none` ÔÇö nunca cria nem altera tabelas.

Os triggers Oracle s├úo respons├íveis por:
- Criar `TB_GST_CONFIGURACAO` automaticamente ao inserir um talh├úo
- Gerar alertas ao inserir leituras com umidade abaixo do threshold
- Gerar alertas ao processar imagem com NDVI abaixo do threshold
- Registrar logs em `TB_GST_LOG_ALERTA` ao mudar status de alerta

### Fluxo de Uso B├ísico

```
1. POST /auth/login                        ÔåÆ obter tokens
2. POST /auth/register (ADMIN)             ÔåÆ criar usu├írios
3. POST /produtores                        ÔåÆ cadastrar produtor
4. POST /propriedades                      ÔåÆ cadastrar propriedade
5. POST /talhoes                           ÔåÆ cadastrar talh├úo (trigger cria configura├º├úo)
6. POST /sensores                          ÔåÆ cadastrar sensor ESP32
7. POST /leituras                          ÔåÆ registrar leitura (trigger pode gerar alerta)
8. GET  /alertas/produtor/me/pendentes     ÔåÆ consultar alertas
9. PATCH /alertas/{id}/resolver            ÔåÆ resolver alerta
```

---

## 🐳 DevOps — Execução em Nuvem (Azure)

> Esta seção cobre exclusivamente o ambiente Docker provisionado na Microsoft Azure para a disciplina de DevOps Tools & Cloud Computing.
> O ambiente local e o deploy no Render são descritos nas seções anteriores.

### How To

Este guia descreve o passo a passo completo para provisionar a infraestrutura na Azure, subir os containers Docker, realizar o seed do banco Oracle e validar o ambiente em produção. Siga os passos em ordem.

### Visão geral

Dois containers Docker sobem em uma VM Ubuntu na Azure:

| Container | Imagem | Porta |
|---|---|---|
| `api-geosat-561940` | Dockerfile multi-stage deste repositório | 8080 |
| `oracle-geosat-561940` | `gvenzl/oracle-xe:21-slim` | 1521 |

Ambos executam na rede isolada `geosat-network`. O Oracle usa volume nomeado `oracle-data` para persistência. A API só sobe após o Oracle estar `healthy`.

### Pré-requisitos

- Azure CLI instalado e autenticado (`az login`)
- Git Bash ou terminal Unix-like
- Acesso à subscription Azure for Students

### Passo 1 — Provisionar a VM na Azure

```bash
bash scripts-cli/setup-geosat.sh
```

Cria o Resource Group `rg-geosat-devops` na região North Central US, provisiona a VM `vm-geosat` (Ubuntu 22.04, Standard_B2als_v2, 4 GB RAM), abre as portas 8080 e 1521, instala Docker e ferramentas (Git, nano, curl, wget, htop).

```bash
# Validar infraestrutura (7 verificacoes)
bash scripts-cli/validate-geosat.sh
```

### Passo 2 — Conectar na VM via SSH

```bash
ssh azureuser@<IP_PUBLICO>
# Senha: GeoSat@2026!
```

O IP público é exibido ao final da execução do `setup-geosat.sh`.

### Passo 3 — Clonar o repositório e configurar variáveis

```bash
cd ~
git clone https://github.com/olavoneves/geosat-java.git
cd geosat-java
cp .env.example .env
nano .env
```

Preencha o `.env` com as credenciais do Oracle do container:

```env
# Escopo Oracle
ORACLE_PASSWORD=SuaSenhaOracleAdmin
APP_USER=geosat
APP_USER_PASSWORD=SuaSenhaAppUser
ORACLE_DATABASE=GEOSATDB

# Escopo API Java
DB_GST_URL=jdbc:oracle:thin:@oracle-db:1521/GEOSATDB
DB_GST_USERNAME=geosat
DB_GST_PASSWORD=SuaSenhaAppUser
GEOSAT_AUTH_SECRET=geosat-2026-fiap-secret-key
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

> ⚠️ **Nunca commite o `.env`** — ele está no `.gitignore`.

### Passo 4 — Subir os containers

```bash
docker compose up -d --build
```

Aguarde o Oracle ficar `healthy` (~2-3 min) e a API inicializar (~30s depois):

```bash
# Acompanhar status
docker compose ps

# Confirmar API no ar
curl http://localhost:8080/actuator/health
```

### Passo 5 — Inserir usuário ADMIN no banco (seed)

O banco Oracle sobe vazio. O usuário master precisa ser inserido antes do primeiro uso da API:

```bash
docker exec -i oracle-geosat-561940 sqlplus geosat/SuaSenhaAppUser@//localhost:1521/GEOSATDB << EOF
INSERT INTO TB_GST_USUARIO_JAVA (
  ID_USUARIO, NM_NOME, DS_EMAIL, DS_SENHA_HASH, DS_ROLE, FL_ATIVO, DT_CRIACAO
) VALUES (
  1, 'Master Admin', 'master@geosat.com',
  '$2b$10$QGgsKq6f1QtCXbuUhB1CvO1Jge9Ke/O8pJIC3xcF2vX9Tx5pQvpEe',
  'ADMIN', 'S', SYSDATE
);
COMMIT;
EXIT;
EOF
```

Credenciais do ADMIN: `master@geosat.com` / `master123`

### Passo 6 — Validar requisitos dos containers

```bash
# Containers rodando em background
docker compose ps

# Usuario nao-root (deve retornar: geosat)
docker exec api-geosat-561940 whoami

# Diretorio de trabalho
docker exec api-geosat-561940 pwd

# Volume nomeado
docker volume ls

# Rede isolada
docker network ls | grep geosat
```

### Passo 7 — Testar CRUD via API

Acesse o Swagger no navegador para testar interativamente:

```
http://<IP_PUBLICO>:8080/swagger-ui.html
```

Ou execute o script de cenas para gravação do vídeo demonstrativo:

```bash
# Cenas de gravacao (executar em sequencia)
bash scenes/c1.sh   # Provisionar infraestrutura
bash scenes/c2.sh   # Clonar, subir containers e seed
bash scenes/c3.sh   # Validar requisitos
bash scenes/c4.sh   # CRUD completo
bash scenes/c5.sh   # Persistencia no Oracle + encerramento
```

### Passo 8 — Evidência de persistência (SQL Developer)

Conecte ao Oracle do container via SQL Developer:

| Campo | Valor |
|---|---|
| Host | `<IP_PUBLICO>` |
| Port | `1521` |
| SID | `GEOSATDB` |
| Usuário | `geosat` |
| Senha | `APP_USER_PASSWORD` do `.env` |

Execute o script `scripts-cli/consulta-evidencia.sql` para evidenciar a persistência em todas as tabelas.

### Passo 9 — Remover recursos (obrigatório após a entrega)

```bash
# Voltar ao Git Bash local (fora da VM)
bash scripts-cli/cleanup-geosat.sh

# Verificar remocao completa
az group show --name rg-geosat-devops --query properties.provisioningState --output tsv
# Quando retornar "not found" = tudo removido
```

> ⚠️ A não remoção gera custos na subscription Azure for Students.

### Diagrama macro da infraestrutura

![Arquitetura GeoSat](geosat-devops/docs/arquitetura_geogast.drawio.png)

---

## 🌐 Contexto do Sistema GeoSat

O GeoSat ├® composto por tr├¬s m├│dulos integrados:

- **Esta API Java** ÔÇö core da plataforma, consumida pelo app mobile React Native dos produtores rurais
- **API .NET** ÔÇö painel administrativo para cooperativas e gestores, respons├ível por processar imagens satelitais
- **ESP32** ÔÇö sensores IoT instalados nos talh├Áes que enviam leituras periodicamente

Ambas as APIs acessam o mesmo banco Oracle. Os triggers Oracle s├úo o motor central de gera├º├úo de alertas.

---

*Global Solution 2026/1 | FIAP | 2TDS Fevereiro*
