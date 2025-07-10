# Encurtador de URL

Este é um aplicativo full-stack de encurtamento de URLs, com backend em Spring Boot e frontend em React.

## Estrutura do Projeto

- `backend/`: Contém a aplicação Spring Boot.
- `frontend/`: Contém a aplicação React.
- `docker-compose.yml`: Define os serviços Docker do projeto (backend, frontend, Redis).

## Tecnologias Utilizadas

### Backend

- Java 21
- Spring Boot
- Spring Data Redis
- Maven

### Frontend

- React
- npm / yarn

### Banco de Dados / Cache

- Redis

## Decisões Arquiteturais

Para construir esta aplicação, foram feitas algumas escolhas técnicas importantes que visam a robustez, escalabilidade e manutenibilidade do projeto:

1.  **Por que Redis?**
    -   O Redis foi escolhido como banco de dados principal por sua alta performance como um keystore in-memory. Para um encurtador de URLs, onde a velocidade de leitura é crítica para o redirecionamento, o Redis é ideal. Além disso, sua funcionalidade nativa de expiração de chaves (`TTL - Time To Live`) se encaixa perfeitamente no requisito de URLs que expiram, simplificando a lógica da aplicação.

2.  **Roteamento com Prefixo `/r/`**
    -   As URLs de redirecionamento utilizam o prefixo `/r/` (ex: `localhost/r/xyz123`). Essa abordagem evita conflitos de rota entre o frontend (uma Single Page Application em React) e os endpoints do backend. Garante que uma requisição de redirecionamento seja sempre direcionada ao backend, enquanto outras rotas (como `/`, `/sobre`, etc.) podem ser tratadas pelo frontend, uma prática padrão para aplicações full-stack modernas.

3.  **Validação no DTO (Backend)**
    -   A validação dos dados de entrada (como o formato da URL) é feita na camada de DTO (`Data Transfer Object`) com anotações (`@NotBlank`, `@Pattern`). Isso segue o princípio de "fail-fast", garantindo que dados inválidos sejam rejeitados na borda da aplicação (Controller), mantendo a camada de serviço limpa e focada exclusivamente na lógica de negócio.

4.  **Spring Boot Actuator**
    -   O projeto inclui o Spring Boot Actuator para expor endpoints de monitoramento (`/actuator/health`, `/actuator/info`). Isso demonstra conhecimento em práticas de observabilidade e preparação do sistema para um ambiente de produção, onde monitorar a saúde da aplicação é fundamental.

## Primeiros Passos

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
-   `GET /r/{shortUrl}`: Redireciona para a URL original.

## Testes Unitários

### Executando os Testes do Backend

Para rodar os testes unitários do backend, no diretório `backend/` execute:

```bash
cd backend
./mvnw test
``` 
