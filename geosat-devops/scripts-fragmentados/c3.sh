#!/bin/bash
# ============================================================
#  CENA 3 — Validar requisitos do entregavel
#  LOCAL: Dentro da VM (continuacao da Cena 2)
# ============================================================
#
#  >>> VOCE AINDA ESTA DENTRO DA VM <
#  cd ~/geosat-java

# Containers rodando em background
echo ""
echo ">>> Containers rodando em background (modo -d):"
docker compose ps

# Logs de ambos os containers
echo ""
echo ">>> Logs do container da API:"
docker logs api-geosat-561940

echo ""
echo ">>> Logs do container do banco:"
docker logs oracle-geosat-561940

# Acesso aos containers via docker container exec
echo ""
echo ">>> [api-geosat-561940] whoami | pwd | ls -l:"
docker container exec api-geosat-561940 whoami
docker container exec api-geosat-561940 pwd
docker container exec api-geosat-561940 ls -l

echo ""
echo ">>> [oracle-geosat-561940] whoami | pwd | ls -l:"
docker container exec oracle-geosat-561940 whoami
docker container exec oracle-geosat-561940 pwd
docker container exec oracle-geosat-561940 ls -l

# Volume nomeado
echo ""
echo ">>> Volume nomeado (oracle-data):"
docker volume ls

# Rede isolada
echo ""
echo ">>> Rede Docker (geosat-network):"
docker network ls | grep geosat

echo ""
echo "============================================="
echo "  VALIDACAO CONCLUIDA"
echo "============================================="

# ------------------------------------------------------------
#  >>> CONTINUA DENTRO DA VM PARA A CENA 4 <
# ------------------------------------------------------------