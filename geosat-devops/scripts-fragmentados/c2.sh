#!/bin/bash
# ============================================================
#  CENA 2 — Conectar na VM, clonar repo e subir containers
#  LOCAL: Git Bash no Windows → SSH na VM
# ============================================================
#
#  PASSO 1 — Conectar via SSH:
#  ssh azureuser@IP_PUBLICO
#  Senha: GeoSat@2026!
#
# ------------------------------------------------------------
#  >>> A PARTIR DAQUI, VOCE ESTA DENTRO DA VM <
# ------------------------------------------------------------

# PASSO 2 — Clonar o repositorio
cd ~
git clone https://github.com/olavoneves/geosat-java.git
cd geosat-java

# PASSO 3 — Subir containers em background
docker compose up -d --build

# PASSO 4 — Inserir usuario ADMIN no banco (seed)
echo ""
echo ">>> Inserindo usuario ADMIN no Oracle..."
echo ""
docker exec -i oracle-geosat-561940 sqlplus geosat/GeoSat2026@//localhost:1521/GEOSATDB << EOF
INSERT INTO TB_GST_USUARIO_JAVA (
  ID_USUARIO, NM_NOME, DS_EMAIL, DS_SENHA_HASH, DS_ROLE, FL_ATIVO, DT_CRIACAO
) VALUES (
  SQ_USUARIO_JAVA.NEXTVAL, 'Master Admin', 'master@geosat.com',
  '\$2b\$10\$QGgsKq6f1QtCXbuUhB1CvO1Jge9Ke/O8pJIC3xcF2vX9Tx5pQvpEe',
  'ADMIN', 'S', SYSDATE
);
COMMIT;
EXIT;
EOF
echo ">>> ADMIN inserido com sucesso!"

# ------------------------------------------------------------
#  >>> CONTINUA DENTRO DA VM PARA A CENA 3 <
# ------------------------------------------------------------