-- ============================================================
--  GEOSAT — Script de Evidência de Persistência
--  Executar no SQL Developer após rodar a Cena 4
--
--  Conexão:
--  Host:         <IP_PUBLICO_DA_VM>
--  Port:         1521
--  Nome Serviço: GEOSATDB
--  Usuário:      geosat
--  Senha:        GeoSat2026
-- ============================================================


-- 1) Usuarios cadastrados
SELECT
  ID_USUARIO,
  NM_NOME,
  DS_EMAIL,
  DS_ROLE,
  FL_ATIVO,
  DT_CRIACAO
FROM TB_GST_USUARIO_JAVA
ORDER BY ID_USUARIO;


-- 2) Produtores vinculados
SELECT
  ID_PRODUTOR,
  NM_NOME,
  NR_CPF,
  DS_EMAIL,
  NR_TELEFONE,
  FL_ATIVO,
  ID_USUARIO
FROM TB_GST_PRODUTOR
ORDER BY ID_PRODUTOR;


-- 3) Propriedades cadastradas
SELECT
  ID_PROPRIEDADE,
  NM_NOME,
  NM_MUNICIPIO,
  SG_ESTADO,
  NR_AREA_HA,
  FL_ATIVA,
  ID_PRODUTOR
FROM TB_GST_PROPRIEDADE
ORDER BY ID_PROPRIEDADE;


-- 4) Talhoes com relacionamento para propriedade
SELECT
  t.ID_TALHAO,
  t.NM_NOME,
  t.DS_CULTURA,
  t.NR_AREA_HA,
  t.FL_ATIVO,
  p.NM_NOME AS NM_PROPRIEDADE
FROM TB_GST_TALHAO t
JOIN TB_GST_PROPRIEDADE p ON t.ID_PROPRIEDADE = p.ID_PROPRIEDADE
ORDER BY t.ID_TALHAO;


-- 5) Sensores registrados
SELECT
  ID_SENSOR,
  CD_IDENTIFICADOR_HW,
  DS_LOCALIZACAO,
  FL_ATIVO,
  DT_INSTALACAO,
  ID_TALHAO
FROM TB_GST_SENSOR
ORDER BY ID_SENSOR;


-- 6) Leituras de sensor (evidencia do ESP32)
SELECT
  ID_LEITURA,
  DT_LEITURA,
  DT_RECEBIDA,
  NR_TEMP_AR,
  NR_UMIDADE_SOLO,
  NR_LUMINOSIDADE,
  FL_TRANSMITIDA,
  ID_SENSOR
FROM TB_GST_LEITURA_SENSOR
ORDER BY DT_RECEBIDA DESC;