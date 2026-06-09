#!/bin/bash
# ============================================================
#  CENA 4 — Testar CRUD completo via API (IP publico)
#  LOCAL: Dentro da VM (continuacao da Cena 3)
# ============================================================
#
#  >>> VOCE AINDA ESTA DENTRO DA VM <
#
#  IMPORTANTE: O token JWT expira em 30 minutos.

IP_PUBLICO=$(curl -s ifconfig.me)

echo ""
echo "============================================="
echo "  CRUD — http://$IP_PUBLICO:8080"
echo "============================================="

# PASSO 1 — Login ADMIN
echo ""
echo ">>> POST — Login ADMIN:"
ADMIN_RESPONSE=$(curl -s -X POST http://$IP_PUBLICO:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "master@geosat.com", "senha": "master123"}')
echo $ADMIN_RESPONSE | python3 -m json.tool
ADMIN_TOKEN=$(echo $ADMIN_RESPONSE | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])")
echo ">>> Token ADMIN obtido!"

# PASSO 2 — Criar usuario PRODUTOR
echo ""
echo ">>> POST — Criar usuario PRODUTOR:"
curl -s -X POST http://$IP_PUBLICO:8080/auth/register \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nmNome": "Joao Silva",
    "dsEmail": "joao@geosat.com",
    "dsSenha": "GeoSat2026",
    "dsRole": "USER"
  }' | python3 -m json.tool

# PASSO 3 — Login PRODUTOR
echo ""
echo ">>> POST — Login PRODUTOR:"
PROD_RESPONSE=$(curl -s -X POST http://$IP_PUBLICO:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "joao@geosat.com", "senha": "GeoSat2026"}')
echo $PROD_RESPONSE | python3 -m json.tool
TOKEN=$(echo $PROD_RESPONSE | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])")
echo ">>> Token PRODUTOR obtido!"

# PASSO 4 — Criar produtor
echo ""
echo ">>> POST — Criar produtor:"
curl -s -X POST http://$IP_PUBLICO:8080/produtores \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nmNome": "Joao Silva",
    "nrCpf": "12345678901",
    "dsEmail": "joao@geosat.com",
    "nrTelefone": "11999990000"
  }' | python3 -m json.tool

# PASSO 5 — Criar propriedade
echo ""
echo ">>> POST — Criar propriedade:"
curl -s -X POST http://$IP_PUBLICO:8080/propriedades \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nmNome": "Fazenda Horizonte",
    "nmMunicipio": "Ribeirao Preto",
    "sgEstado": "SP",
    "nrAreaHa": 120.5
  }' | python3 -m json.tool

# PASSO 6 — Criar talhao
echo ""
echo ">>> POST — Criar talhao:"
curl -s -X POST http://$IP_PUBLICO:8080/talhoes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nmNome": "Talhao A1",
    "dsCultura": "Soja",
    "nrAreaHa": 30.0,
    "idPropriedade": 1
  }' | python3 -m json.tool

# PASSO 7 — Criar sensor ESP32
echo ""
echo ">>> POST — Criar sensor ESP32:"
curl -s -X POST http://$IP_PUBLICO:8080/sensores \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cdIdentificadorHw": "AA:BB:CC:DD:EE:FF",
    "dsLocalizacao": "Setor Norte Talhao A1",
    "idTalhao": 1
  }' | python3 -m json.tool

# PASSO 8 — Registrar leitura (simula ESP32)
echo ""
echo ">>> POST — Registrar leitura de sensor:"
curl -s -X POST http://$IP_PUBLICO:8080/leituras \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idSensor": 1,
    "dtLeitura": "2026-06-08T10:00:00",
    "nrTempAr": 34.5,
    "nrUmidadeSolo": 15.2,
    "nrLuminosidade": 920.0
  }' | python3 -m json.tool

# PASSO 9 — Atualizar talhao
echo ""
echo ">>> PUT — Atualizar talhao:"
curl -s -X PUT http://$IP_PUBLICO:8080/talhoes/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nmNome": "Talhao A1",
    "dsCultura": "Soja - Safra 2026",
    "nrAreaHa": 32.0,
    "idPropriedade": 1
  }' | python3 -m json.tool

# PASSO 10 — Desativar produtor (soft delete)
echo ""
echo ">>> DELETE — Desativar produtor (id=1):"
curl -s -X DELETE http://$IP_PUBLICO:8080/produtores/1 \
  -H "Authorization: Bearer $ADMIN_TOKEN" -w "\nHTTP Status: %{http_code}\n"
