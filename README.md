# 📌 Sistema de Gestão de Espaço Físico

Este projeto tem como objetivo o desenvolvimento de um **sistema de gestão de reservas de espaços físicos** em uma instituição, permitindo que usuários (solicitantes) façam solicitações de uso de espaços como laboratórios, salas de aula, auditórios, entre outros, e que essas solicitações sejam avaliadas por gestores.
---
## OBS:

Caso o código apresente erro na IDE utilizada (tanto o Eclipse quanto o Intellij), recomendamos que instale o JDBC driver do sql, segue o link a seguir (Versão: 42.7.2): https://jdbc.postgresql.org/download/
---

## 📋 Sumário

- [🚀 Funcionalidades](#-funcionalidades)
- [📚 Histórias de Usuário](#-histórias-de-usuário)
- [🧱 Modelo de Dados Físico](#-modelo-de-dados-físico)
- [💾 Scripts SQL](#-scripts-sql)
- [🔧 Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [💻 Como Executar](#-como-executar)
- [📎 Consultas SQL (DQL)](#-consultas-sql-dql)
- [📂 Organização do Projeto](#-organização-do-projeto)
- [🛠️ Contribuição](#️-contribuição)

---

## 🚀 Funcionalidades

- Cadastro e consulta de espaços físicos.
- Registro de solicitantes, gestores e usuários do sistema.
- Solicitação de reserva de espaço com data, hora e justificativa.
- Avaliação de solicitações (Aprovação/Rejeição) por gestores.
- Registro de auditoria com ações dos usuários.
- Histórico completo de reservas e avaliações.

---

## 📚 Histórias de Usuário

### 1. Como **solicitante**, desejo fazer uma reserva de espaço, informando data, hora e justificativa.

### 2. Como **gestor**, desejo visualizar todas as solicitações pendentes e aprová-las ou rejeitá-las, com justificativa.

### 3. Como **usuário do sistema**, desejo visualizar o histórico de todas as reservas feitas.

### 4. Como **administrador**, quero monitorar todas as ações dos usuários para fins de auditoria.

---

## 🧱 Modelo de Dados Físico

O banco de dados está estruturado com as seguintes tabelas:

- `solicitantes`
- `gestores`
- `espacos`
- `solicitacoes`
- `usuarios`
- `auditoria`

O modelo segue a **terceira forma normal (3FN)** para evitar redundâncias e inconsistências.

---

## 💾 Scripts SQL

Todos os scripts estão preparados para o **PostgreSQL**. Incluem:

- Criação de tabelas (DDL)
- Inserção de dados iniciais
- Consultas (DQL)

O script pode ser executado diretamente sem erros.

> ✅ Testado com PostgreSQL 14+

---

## 🔧 Tecnologias Utilizadas

- **Java 17**
- **JDBC (Java Database Connectivity)**
- **PostgreSQL**
- **SQL**

---

## 💻 Como Executar

1. Configure o PostgreSQL com um banco chamado `gestao_de_espaco_fisico`.
2. Execute o script SQL `database.sql` (ou similar).
3. Compile o projeto Java.
4. Configure o arquivo Java com sua senha de banco:
   ```java
   private static final String PASSWORD = "SUA_SENHA_DO_BANCO";
