# =============================================================================
# GeoSat API — Dockerfile (multi-stage build)
# Estágio 1: compila o projeto com Maven e gera o JAR
# Estágio 2: executa o JAR com JRE leve (sem Maven, sem JDK)
# =============================================================================

# ── ESTÁGIO 1: BUILD ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copia arquivos de dependência primeiro (aproveita cache de layers se pom.xml não mudou)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Baixa dependências sem compilar o código
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copia o código fonte e compila gerando o JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ── ESTÁGIO 2: RUNTIME ────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Cria usuário não-root por boas práticas de segurança
RUN addgroup -S geosat && adduser -S geosat -G geosat

# Copia apenas o JAR gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar

# Executa como usuário não-root
USER geosat

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
  
ENTRYPOINT ["java", "-jar", "app.jar"]
