# Encurtador de URL

Este é um aplicativo full-stack de encurtamento de URLs, com backend em Spring Boot e frontend em React.

## Estrutura do Projeto

- `backend/`: Contém a aplicação Spring Boot.
- `frontend/`: Contém a aplicação React.
- `docker-compose.yml`: Define os serviços Docker do projeto (backend, frontend, Redis).

## Tecnologias Utilizadas

### Backend

- Java 17+
- Spring Boot
- Spring Data Redis
- Maven

### Frontend

- React
- npm / yarn

### Banco de Dados / Cache

- Redis

## Primeiros Passos

### Pré-requisitos

Certifique-se de ter instalado:

- Docker e Docker Compose
- Java 17+ (para desenvolvimento do backend fora do Docker)
- Maven (para desenvolvimento do backend fora do Docker)
- Node.js e npm/yarn (para desenvolvimento do frontend fora do Docker)

### Configuração e Execução com Docker Compose

A forma mais fácil de rodar a aplicação é usando Docker Compose:

1. **Gerar o JAR do Backend:**
    No diretório `backend/`, execute:
    ```bash
    cd backend
    ./mvnw clean install -DskipTests
    cd ..
    ```

2. **Subir os Serviços:**
    No diretório raiz do projeto, execute:
    ```bash
    docker-compose up --build
    ```
    Isso irá construir as imagens Docker do backend e frontend, e iniciar todos os serviços (backend, frontend, Redis).

    O backend ficará disponível em `http://localhost:8080` e o frontend em `http://localhost:80`.

### Executando o Backend Separadamente (Desenvolvimento)

1. **Iniciar o Redis:**
    Se não for usar o Docker Compose para tudo, você pode iniciar o Redis separadamente (exemplo via Docker):
    ```bash
    docker run --name my-redis -p 6379:6379 -d redis/redis-stack-server
    ```

2. **Rodar o Backend:**
    No diretório `backend/`, execute:
    ```bash
    cd backend
    ./mvnw spring-boot:run
    ```
    O backend ficará disponível em `http://localhost:8080`.

### Executando o Frontend Separadamente (Desenvolvimento)

1. **Instalar Dependências:**
    No diretório `frontend/`, execute:
    ```bash
    cd frontend
    npm install 
    ```

2. **Iniciar o Frontend:**
    ```bash
    npm run dev 
    ```
    O frontend ficará disponível em `http://localhost:3000`.

## Endpoints da API (Backend)

-   `POST /shorten`: Cria uma nova URL curta.
    -   Corpo da requisição: `{"longUrl": "sua-url-longa", "expiresAt": horas_para_expirar}`
-   `GET /{shortUrl}`: Redireciona para a URL original.

## Testes Unitários

### Executando os Testes do Backend

Para rodar os testes unitários do backend, no diretório `backend/` execute:

```bash
cd backend
./mvnw test
``` 