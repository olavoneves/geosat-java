#!/bin/bash
# ============================================================
#  CENA 5 — Persistencia no Oracle + Encerramento
#  LOCAL: Dentro da VM → depois volta pro Git Bash local
# ============================================================
#
#  >>> VOCE AINDA ESTA DENTRO DA VM <<<

echo ""
echo "============================================="
echo "  PERSISTENCIA — Consulta direta no Oracle"
echo "============================================="
echo ""

echo ">>> Conectando no Oracle..."
echo ""

docker exec -i oracle-geosat-561940 sqlplus geosat/GeoSat2026@//localhost:1521/GEOSATDB <<EOF
SET LINESIZE 200;
SET PAGESIZE 50;
SELECT * FROM TB_GST_USUARIO_JAVA;
SELECT * FROM TB_GST_PRODUTOR;
SELECT * FROM TB_GST_PROPRIEDADE;
SELECT * FROM TB_GST_TALHAO;
SELECT * FROM TB_GST_SENSOR;
SELECT * FROM TB_GST_LEITURA_SENSOR;
EXIT;
EOF

echo ""
echo "============================================="
echo "  PERSISTENCIA CONFIRMADA!"
echo "============================================="
echo ""

# ----------------------------------------------------------
#  PASSO 2 — Sair da VM
# ----------------------------------------------------------
#
#  Digitar: exit
#
# ------------------------------------------------------------
#  >>> AGORA VOCE ESTA DE VOLTA NO GIT BASH LOCAL <<<
# ------------------------------------------------------------
#
#  PASSO 3 — Deletar recursos (OBRIGATORIO)
#
#  bash /c/Users/StartSe/entregáveis/globalsolution/devops/geosat-devops-final/scripts-cli/cleanup-geosat.sh
#
#  PASSO 4 — Tirar print da evidencia de remocao
#
#  Verificar com:
#  az group show --name rg-geosat-devops --query properties.provisioningState --output tsv
#
#  Quando retornar "not found" = tudo removido. Tirar print!
#
# ============================================================
