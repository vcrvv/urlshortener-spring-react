# URL Shortener - Encurtador de URL Full-stacks

Este é um projeto de um encurtador de URLs completo, desenvolvido com **Spring Boot** para o backend e **React** para o frontend. A aplicação permite aos usuários transformar URLs longas em links curtos, fáceis de compartilhar.

O projeto foi totalmente containerizado com **Docker**, garantindo um ambiente de desenvolvimento e produção consistente e de fácil configuração.

## Funcionalidades

- **Encurtamento de URLs**: Gera um link curto e único para qualquer URL longa.
- **Redirecionamento Rápido**: Redireciona os usuários da URL curta para a original de forma eficiente.
- **Expiração de Links**: Permite definir um tempo de vida para cada URL encurtada.
- **Rate Limiting**: Limita o número de requisições por IP para prevenir abusos.
- **Frontend Intuitivo**: Interface de usuário limpa e reativa, construída com React e Material-UI.
- **Modo Escuro**: Suporte a tema claro e escuro para melhor experiência do usuário.
- **Documentação da API**: API documentada com Swagger para fácil entendimento e teste dos endpoints.

## Arquitetura

A aplicação é dividida em três serviços principais, orquestrados com `docker-compose`:

1.  **`backend`**: Serviço em **Spring Boot** responsável por toda a lógica de negócio.
    -   Valida e processa as URLs longas.
    -   Gera os códigos curtos.
    -   Armazena e recupera as URLs do Redis.
    -   Expõe uma API REST para o frontend.
2.  **`frontend`**: Aplicação em **React (Vite)** que consome a API do backend.
    -   Fornece o formulário para o usuário inserir a URL.
    -   Exibe a URL encurtada e permite copiá-la facilmente.
3.  **`redis`**: Instância do **Redis** usada como banco de dados em memória.
    -   Armazena a correspondência entre a URL curta e a original.
    -   Garante alta performance para leitura e escrita.


## Tecnologias Utilizadas

| Categoria      | Tecnologia                                                                                             |
| -------------- | ------------------------------------------------------------------------------------------------------ |
| **Backend**    | [Java 21](https://www.oracle.com/java/), [Spring Boot 3](https://spring.io/projects/spring-boot), Maven  |
| **Frontend**   | [React](https://reactjs.org/), [Vite](https://vitejs.dev/), [Material-UI](https://mui.com/)             |
| **Banco de Dados** | [Redis](https://redis.io/)                                                                             |
| **Container**  | [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/)                 |
| **Testes**     | [JUnit 5](https://junit.org/junit5/), [Mockito](https://site.mockito.org/), [Jest](https://jestjs.io/), [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/) |
| **API Docs**   | [Swagger (SpringDoc)](https://springdoc.org/)                                                          |


## Como Executar o Projeto

A maneira mais simples de executar a aplicação é utilizando Docker.

### Pré-requisitos

-   [Docker](https://www.docker.com/get-started) e [Docker Compose](https://docs.docker.com/compose/install/) instalados.
-   [Java 21](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html) e [Maven](https://maven.apache.org/download.cgi) (apenas para build local).

### Passos

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/seu-usuario/urlshortener-spring-react.git
    cd urlshortener-spring-react
    ```

2.  **Construa e inicie os containers:**
    ```bash
    docker-compose up --build
    ```

3.  **Acesse a aplicação:**
    -    **Frontend**: `http://localhost:80`
    -    **Backend API**: `http://localhost:8080`

A aplicação estará pronta para uso!

## Documentação da API (Swagger)

Com a aplicação em execução, a documentação interativa da API, gerada pelo Swagger, pode ser acessada em:

-   **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Nesta página é possível visualizar todos os endpoints, seus parâmetros, e testá-los diretamente pelo navegador.

## Testes

O projeto possui uma suíte de testes robusta para garantir a qualidade e a estabilidade do código.

### Testes do Backend
Para rodar os testes unitários e de integração do backend, navegue até a pasta `backend` e execute:
```bash
./mvnw test
```

### Testes do Frontend
Para rodar os testes dos componentes do frontend, navegue até a pasta `frontend` e execute:
```bash
npm install
npm test
```
