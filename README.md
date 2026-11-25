# Armazém de Ingredientes

API do desafio técnico Vega para estoque de uma fábrica de alimentos, com arquitetura hexagonal, H2, Spring Security, Swagger, painel React, Log4j e observabilidade no Grafana.

## Acessos

| O quê | Endereço | Usuário | Senha |
| --- | --- | --- | --- |
| Painel (desenvolvimento) | http://localhost:5173 | `admin` ou `pedro` | `admin123` ou `pedro123` |
| Painel (Docker) | http://localhost:8088 | `admin` ou `pedro` | `admin123` ou `pedro123` |
| API | http://localhost:8080 | token JWT do login | — |
| Login | POST http://localhost:8080/auth/login | `admin` / `pedro` | `admin123` / `pedro123` |
| Swagger | http://localhost:8080/swagger-ui.html | público | — |
| OpenAPI | http://localhost:8080/v3/api-docs | público | — |
| Saúde | http://localhost:8080/actuator/health | público | — |
| Métricas Prometheus | http://localhost:8080/actuator/prometheus | público | — |
| Console H2 (local) | http://localhost:8080/h2-console | `sa` | em branco |
| JDBC H2 local | `jdbc:h2:file:./data/armazem;DB_CLOSE_DELAY=-1` | `sa` | em branco |
| JDBC H2 no Docker | `jdbc:h2:file:/data/armazem;DB_CLOSE_DELAY=-1` | `sa` | em branco |
| Grafana | http://localhost:3000 | `admin` | `admin123` |
| Prometheus | http://localhost:9090 | público | — |
| Loki | http://localhost:3100 | público | — |

O administrador tem o papel `ROLE_ADMIN`. O operador `pedro` tem o papel `ROLE_OPERADOR`. Os dois podem usar a API. No Grafana, o painel **Armazém de Ingredientes** fica na pasta Armazem. Os logs do Log4j aparecem em Explore, com a consulta `{job="armazem"}`.

No login, envie:

```json
{ "usuario": "admin", "senha": "admin123" }
```

Use o token nas demais rotas: `Authorization: Bearer <token>`.

## Como executar na máquina

Requisitos: Java 21, Maven, Node.js 20 ou superior.

```powershell
cd backend
mvn spring-boot:run
```

Em outro terminal:

```powershell
cd frontend
npm install
npm run dev
```

Abra http://localhost:5173. Os arquivos de log ficam em `backend/logs/armazem.log` e `backend/logs/armazem-error.log`.

## Como executar com Docker

Na raiz do projeto:

```powershell
docker compose up --build
```

O painel fica em http://localhost:8088 e já encaminha `/api` para o backend. Grafana, Prometheus e Loki sobem juntos. O Promtail lê o volume de logs escrito pelo Log4j e entrega ao Loki, que o Grafana consulta.

## Serviços da API

Todas as rotas abaixo, exceto o login, exigem o token.

| Método | Caminho | Função |
| --- | --- | --- |
| POST | `/auth/login` | Autentica e devolve o JWT |
| POST | `/ingredientes` | Cadastra o ingrediente e registra a entrada |
| GET | `/ingredientes` | Lista os ingredientes |
| GET | `/ingredientes/{id}` | Consulta um ingrediente |
| GET | `/ingredientes/volume` | Total armazenado por tipo |
| POST | `/ingredientes/{id}/saida` | Registra a saída e o histórico |
| GET | `/compartimentos` | Estado de C1 a C5 |
| GET | `/compartimentos/disponiveis?quantidade=&tipo=` | Compartimentos com espaço suficiente |
| GET | `/compartimentos/disponiveis-para-venda?tipo=` | Compartimentos com saldo do tipo |
| GET | `/historico?sortBy=date\|compartimento&order=asc\|desc` | Histórico ordenado |

Exemplo de entrada:

```json
{
  "nome": "Farinha de Trigo",
  "tipo": "SECOS",
  "quantidade": 400,
  "compartimentoId": 1,
  "responsavel": "pedro"
}
```

O volume responde com `type` e `totalQuantity`, na ordem LIQUIDO, REFRIGERADO e SECO. Os tipos aceitos na entrada são `SECO`/`SECOS`, `LIQUIDO`/`LIQUIDOS` e `REFRIGERADO`/`REFRIGERADOS`, com ou sem acento.

## Regras

- C1 a C5 começam vazios.
- Secos cabem até 600 kg, líquidos até 500 L e refrigerados até 400 kg.
- Um compartimento com saldo só recebe o mesmo tipo.
- Se ele recebeu seco hoje, mesmo depois de esvaziado, outro tipo só entra amanhã. Novo seco no mesmo dia continua permitido.
- Entrada e saída acontecem na mesma transação do histórico. Se a regra falha, nada é gravado.
- A capacidade estourada ou a mistura de tipos responde `400` com a mensagem da regra.
- Duas movimentações no mesmo compartimento disputam um bloqueio pessimista, então o segundo pedido enxerga o saldo já atualizado.

## Arquitetura

O domínio não depende de Spring nem de JPA. As portas de entrada ficam em `domain/port/in` e as de saída em `domain/port/out`. Os serviços em `application` implementam os casos de uso. O adaptador web publica REST, segurança e Swagger. O adaptador de persistência usa H2 e JPA. O relógio de `America/Sao_Paulo` torna a regra do "hoje" testável.

O Log4j escreve informação no console e em `armazem.log`. Erros também vão para `armazem-error.log`. O actuator exporta as métricas que o Prometheus coleta e o Grafana mostra.

## Testes

```powershell
cd backend
mvn test
```

Há testes das regras de domínio, da gravação obrigatória do histórico e de um fluxo HTTP com autenticação, mistura, carência de seco e duas entradas concorrentes no mesmo compartimento.
