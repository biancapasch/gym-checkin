cat > README.md <<'EOF'
# Gym Check-in API

API REST desenvolvida em Java 21 com Spring Boot para gerenciamento de clientes, check-ins de academia e faturamento mensal.  
Projeto focado em boas práticas de backend, regras de negócio claras e testes automatizados.

Este projeto foi criado com fins de estudo e portfólio, simulando um sistema real de academia.

---

## Funcionalidades

### Clientes
- Criação de clientes
- Definição automática do dia de pagamento com base na data informada
- Relacionamento com check-ins e invoices

### Check-ins
- Registro de entrada (IN) e saída (OUT)
- Controle de sessão aberta
- Encerramento automático de sessões antigas (limite de 6 horas)
- Listagem paginada de check-ins
- Busca de sessão aberta do cliente

### Faturamento (Invoices)
- Geração de invoices mensais
- Prevenção de invoices duplicadas para o mesmo cliente e data
- Pagamento de invoice pendente
- Listagem paginada de invoices por cliente

### Job agendado
- Geração automática de invoices
- Execução duas vezes ao dia
- Criação de invoices com base no paymentDay dos clientes
- Tratamento de duplicidade via exceção

---

## Arquitetura

O projeto segue uma arquitetura em camadas:

controller -> service -> repository -> database


Principais conceitos aplicados:
- RESTful API
- DTOs para request e response
- Regras de negócio concentradas na camada de service
- JPA / Hibernate
- Paginação com Pageable
- Tratamento global de exceções
- Profiles (dev e test)
- Docker para banco de dados
- Testes automatizados

---

## Endpoints principais

### Clientes

POST /customers

  ## Check-ins

POST /customers/{customerId}/checkins
GET  /customers/{customerId}/checkins
GET  /customers/{customerId}/checkins/open-session

## Invoices

PATCH /customers/{customerId}/invoices/{invoiceId}/pay
GET   /customers/{customerId}/invoices

# Tratamento de erros

A API retorna erros padronizados no seguinte formato:

{
"status": 404,
"error": "Not Found",
"message": "Invoice not found",
"path": "/customers/1/invoices/99/pay",
"timestamp": "2026-01-28T10:15:30Z"
}

## Exceções tratadas:

NotFoundException

DuplicateInvoiceException

InvoiceAlreadyPaidException

## Testes

Testes de service com JUnit 5 e Mockito

Testes de repository com @DataJpaTest e H2

Testes de controller com @WebMvcTest e MockMvc

## Tecnologias utilizadas

- Java 21

- Spring Boot 3

- Spring Web

- Spring Data JPA

- Bean Validation

- PostgreSQL

- H2 (testes)

- Gradle

- Docker e Docker Compose

- Lombok

- JUnit 5 e Mockito

- Banco de dados com Docker

## Para subir o PostgreSQL localmente:

docker compose up -d

## Configuração padrão:

Database: gym_checkin

User: postgres

Password: postgres

Port: 5432

# Como executar o projeto

## Subir o banco de dados

docker compose up -d

## Executar a aplicação

./gradlew bootRun

# Como rodar os testes

./gradlew test

# Autora

Bianca Paschoal
GitHub: https://github.com/biancapasch
'EOF'

