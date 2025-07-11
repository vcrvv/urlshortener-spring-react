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

2.  **Inicie os containers:**
    ```bash
    docker-compose up --build
    ```

3.  **Acesse a aplicação:**
    -   O **Frontend** estará disponível em `http://localhost:5173`.
    -   O **Backend** estará disponível em `http://localhost:8080`.

---

## 📖 Documentação da API (Swagger)

Com a aplicação em execução, a documentação interativa da API, gerada pelo Swagger UI, pode ser acessada no seguinte endereço:

-   **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Lá você pode visualizar todos os endpoints, seus parâmetros, e testá-los diretamente pelo navegador.

---

## 🧪 Testes

O projeto possui uma suíte de testes robusta para garantir a qualidade do código.

### Backend
Para rodar os testes do backend (unitários e de integração), navegue até a pasta `backend` e execute:
```bash
./mvnw test
```

### Frontend
Para rodar os testes do frontend, navegue até a pasta `frontend` e execute:
```bash
npm test
```
