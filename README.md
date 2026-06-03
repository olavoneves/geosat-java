# GeoSat API — Java REST Backend

API REST do sistema **GeoSat**, plataforma de monitoramento agrícola que combina imagens satelitais (NASA/ESA) com sensores IoT ESP32 para geração de alertas antecipados de risco para produtores rurais brasileiros.

> **FIAP — Global Solution 2026/1 | 2TDS Fevereiro**

---

## 🔗 Links

| Recurso | Link |
|---------|------|
| 🚀 Deploy (produção) | *https://geosat-java.onrender.com* |
| 📖 Swagger UI (produção) | **[SUBSTITUIR — URL do Render + /swagger-ui.html]** |
| 🎥 Vídeo de Apresentação | **[SUBSTITUIR — link do YouTube após gravação]** |
| 🎯 Vídeo Pitch (3 min) | **[SUBSTITUIR — link do YouTube após gravação]** |
| 💻 Repositório GitHub | *https://github.com/olavoneves/geosat-java* |

---

## 📐 Diagrama de Entidades

```mermaid
erDiagram
    TB_GST_USUARIO_JAVA ||--o{ TB_GST_PRODUTOR : "cadastra"
    TB_GST_USUARIO_JAVA ||--o{ TB_GST_ACCESS_TOKEN_JAVA : "possui"
    TB_GST_USUARIO_JAVA ||--o{ TB_GST_REFRESH_TOKEN_JAVA : "possui"
    TB_GST_PRODUTOR ||--o{ TB_GST_PROPRIEDADE : "possui"
    TB_GST_PROPRIEDADE ||--o{ TB_GST_TALHAO : "subdivide"
    TB_GST_TALHAO ||--o{ TB_GST_SENSOR : "recebe"
    TB_GST_TALHAO ||--o{ TB_GST_IMAGEM_SATELITAL : "associa"
    TB_GST_TALHAO ||--o{ TB_GST_ALERTA : "dispara"
    TB_GST_TALHAO ||--|| TB_GST_CONFIGURACAO : "configura"
    TB_GST_SENSOR ||--o{ TB_GST_LEITURA_SENSOR : "gera"
    TB_GST_ALERTA ||--o{ TB_GST_LOG_ALERTA : "registra"
```

---

## 🏗️ Arquitetura

```
br.com.geosat.server
├── config/          # OpenAPI, CORS, Filter registration, AuthProperties
├── controller/      # REST controllers com HATEOAS e Swagger annotations
├── dto/
│   ├── request/     # Java Records com Bean Validation (@NotBlank, @Email, @Pattern...)
│   └── response/    # Java Records com factory from(Entity)
├── exception/       # Exceções customizadas + GlobalExceptionHandler (@RestControllerAdvice)
├── filter/          # AuthTokenFilter (OncePerRequestFilter) + TokenUtils (SHA-256)
├── model/           # Entidades JPA com @Embedded Auditoria (modelagem avançada)
├── repository/      # Spring Data JPA — JpaRepository
└── service/         # Regras de negócio e orquestração
```

### Modelagem Avançada — @Embedded

A classe `Auditoria` é um `@Embeddable` que encapsula os campos `dtCriacao` e `flAtivo` presentes em múltiplas entidades. Usada com `@Embedded` em `UsuarioJava`, `Produtor`, `Propriedade`, `Talhao` e `Sensor`, demonstrando modelagem avançada com reutilização de componentes JPA.

### Autenticação Manual (sem Spring Security)

- Login gera `accessToken` (UUID) + `refreshToken` (UUID)
- Apenas o **hash SHA-256** de cada token é armazenado no banco
- O plain text é retornado ao cliente: `Authorization: Bearer <token>`
- `AuthTokenFilter` intercepta requisições protegidas, busca hash no banco, valida expiração
- **Refresh token rotation**: ao renovar, o refresh antigo é revogado e dois novos tokens são gerados

---

## 🛠️ Tecnologias

| Tecnologia | Versão |
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

## 📋 Endpoints

| Módulo | Base URL | Operações |
|--------|----------|-----------|
| Autenticação | `/auth` | login, refresh, logout, register (ADMIN) |
| Usuários | `/usuarios` | GET, PUT, DELETE (ADMIN) |
| Produtores | `/produtores` | POST, GET, GET/me, PUT, DELETE |
| Propriedades | `/propriedades` | POST, GET, GET/produtor/{id}, PUT, DELETE |
| Talhões | `/talhoes` | POST, GET, GET/propriedade/{id}, PUT, DELETE |
| Sensores | `/sensores` | POST, GET, GET/talhao/{id}, PUT, DELETE |
| Leituras | `/leituras` | POST, GET/{id}, GET/sensor/{id}, GET/sensor/{id}/last |
| Imagens Satelitais | `/imagens` | POST, GET, PATCH/processar, PATCH/erro |
| Alertas | `/alertas` | GET, GET/{id}, GET/talhao, GET/produtor/me, PATCH/visualizar, PATCH/resolver, PATCH/reabrir |
| Configurações | `/configuracoes` | GET/talhao/{id}, PUT/{id} |

Documentação completa interativa: `GET /swagger-ui.html`

---

## ⚙️ Executando Localmente

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Acesso ao banco Oracle GeoSat (schema já criado e populado)

### Variáveis de Ambiente

Crie um arquivo `.env.local` na raiz do projeto (não commitar):

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

## ☁️ Deploy (Render)

1. Faça push do repositório para o GitHub
2. No Render, crie um novo **Web Service** apontando para o repositório
3. Configure o Build Command: `./mvnw package -DskipTests`
4. Configure o Start Command: `java -jar target/*.jar`
5. Em **Environment Variables**, adicione:

| Variável | Valor |
|----------|-------|
| `DB_GST_URL` | `jdbc:oracle:thin:@<host>:<port>/<service>` |
| `DB_GST_USERNAME` | seu RM Oracle |
| `DB_GST_PASSWORD` | sua senha Oracle |

Ou importe o arquivo `.env` diretamente pelo painel do Render.

---

## 🧪 Exemplos de Teste

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
    "nmNome": "João Silva",
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

> O trigger Oracle verifica automaticamente se `nrUmidadeSolo` está abaixo do threshold configurado para o talhão. Se estiver, um alerta é gerado sem ação adicional da API.

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

## 🗄️ Banco de Dados

O schema Oracle **já existe e está populado**. A API usa `ddl-auto=none` — nunca cria nem altera tabelas.

Os triggers Oracle são responsáveis por:
- Criar `TB_GST_CONFIGURACAO` automaticamente ao inserir um talhão
- Gerar alertas ao inserir leituras com umidade abaixo do threshold
- Gerar alertas ao processar imagem com NDVI abaixo do threshold
- Registrar logs em `TB_GST_LOG_ALERTA` ao mudar status de alerta

### Fluxo de Uso Básico

```
1. POST /auth/login                        → obter tokens
2. POST /auth/register (ADMIN)             → criar usuários
3. POST /produtores                        → cadastrar produtor
4. POST /propriedades                      → cadastrar propriedade
5. POST /talhoes                           → cadastrar talhão (trigger cria configuração)
6. POST /sensores                          → cadastrar sensor ESP32
7. POST /leituras                          → registrar leitura (trigger pode gerar alerta)
8. GET  /alertas/produtor/me/pendentes     → consultar alertas
9. PATCH /alertas/{id}/resolver            → resolver alerta
```

---

## 🌐 Contexto do Sistema GeoSat

O GeoSat é composto por três módulos integrados:

- **Esta API Java** — core da plataforma, consumida pelo app mobile React Native dos produtores rurais
- **API .NET** — painel administrativo para cooperativas e gestores, responsável por processar imagens satelitais
- **ESP32** — sensores IoT instalados nos talhões que enviam leituras periodicamente

Ambas as APIs acessam o mesmo banco Oracle. Os triggers Oracle são o motor central de geração de alertas.

---

*Global Solution 2026/1 | FIAP | 2TDS Fevereiro*
