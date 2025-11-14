# MySpendingApp

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Kevindmm_MySpendingApp&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Kevindmm_MySpendingApp)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Kevindmm_MySpendingApp&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Kevindmm_MySpendingApp)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Kevindmm_MySpendingApp&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Kevindmm_MySpendingApp)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Kevindmm_MySpendingApp&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Kevindmm_MySpendingApp)

**MySpendingApp** is a lightweight personal finance tracker built with **Java 17** and **Spring Boot**.\
The goal is to create a clear and maintainable backend while showcasing modern Java 17 features in a practical way.

---

## MVP · v0.1

- **Record transactions**  
  Add, edit, and delete expenses or income entries with date, amount, category, and optional note.

- **Transaction list with filters**  
  Chronological table with sorting, pagination, and quick filters by date range and category.

- **Monthly summary**  
  Totals for expenses, income, and balance—compact overview of where the money goes.

- **Minimal categories + CRUD**  
  Seed set (Food, Transport, Leisure, etc.) with endpoints to add or rename categories.

- **Lightweight persistence**  
  SQLite for development, H2 for tests; schema auto-generated at backend startup.

- **REST API v1**  
  `/transactions` and `/categories` endpoints (GET, POST, PUT, DELETE) returning clean JSON.

- **React frontend (SPA)**  
  Single page with “Add transaction” form, transaction table, and summary section.

- **Smoke tests & CI**  
  Minimal seed data plus a couple of API smoke tests running in GitHub Actions.


---


## 🚀 Development Roadmap

| Phase                              | Purpose | Status |
|------------------------------------|---------|--------|
| Phase 0 – Setup                    | Skeleton, CI, Docker, health-check | ✅ Done |
| Phase 1 – Data Model               | Finalise production-ready schema (users, categories, transactions) on SQLite/H2; seed demo data | ✅ Done |
| Phase 2 – MVP (MySpendingApp CRUD) | Implement login + JWT and full spending CRUD on top of the mature DB | ✍️ In progress |
| Phase 3 – Modernisation            | Replace legacy constructs with Java 17+ features; introduce records, sealed classes | ⬜ Planned |
| Phase 4 – Enhancements             | FX API integration, dashboards, reporting, test-data tools | ⬜ Planned |



### ✅ Phase 0 — Setup (Completed!)
*Goal: bring the project to life locally with CI, health-check, logging, and a working Docker stack.*

- [x] **P0.1**  Spring Boot skeleton + Gradle build
- [x] **P0.2**  `/actuator/health` endpoint exposed and healthy
- [x] **P0.3**  GitHub Actions workflow (build + tests badge)
- [x] **P0.4**  Dockerised stack: backend (JDK 17 + SQLite) & frontend (React + Nginx) via `docker compose`
- [x] **P0.5**  Fix persistent logging: logs now land in `/app/logs`



### ✅ Phase 1 — Data Model (Completed!)
*Goal: lock down a production-ready schema<—>users, categories, transactions and preload demo data so later phases can 
focus on business logic and UI.*

- [x] **P1.1**  Add README with schema diagram + [DB-evolution](docs/DB-evolution.md) log
- [x] **P1.2**  User entity & repository
- [x] **P1.3**  Category entity (per-user)
- [x] **P1.4** Setup dev/test environments, added H2 in-memory DB, test profile & Gradle/Docker test config
- [X] **P1.5** Extend Transaction with `user_id`, `category_id`, `note`
- [x] **P1.6** Mark `conversion` as deprecated (retained for frontend chart; removed/refined in Phase 4)
- [x] **P1.7** add `note` field to Transaction entity for optional free-text details
- [x] **P1.8** TransactionV2: main entity with UUID + User/Category FK (MVP entity from P1.9 onwards), Transaction model marked as deprecated, fixed bugs
- [x] **P1.9** Update `data.sql` with UUIDs + seed demo user, categories & transactionsv2; refactor `application.properties`
- [x] **P1.10** H2 integration tests for User, Category & TransactionV2 repos; created `data-h2.sql`; UUID → VARCHAR(36) migration for user/category/transactionsv2 tables



### ✍️ Phase 2 — MVP (MySpendingApp CRUD + JWT)
*Goal: implement authentication, core spending CRUD, and quality gates on top of the existing `User`, `Category`, and `TransactionV2` entities from Phase 1.*

- [x] **P2.1** SonarQube integration with GitHub Actions; add quality gate badge to README
- [x] **P2.2** JWT authentication: `AuthController`, `JwtTokenProvider`, `JwtAuthenticationFilter`, `SecurityAuthConfig`; `POST /api/v1/auth/login` endpoint
- [ ] **P2.3** `AuthController` implementation of refresh token and logout endpoints; extend `JwtTokenProvider` for refresh tokens; update `SecurityAuthConfig`
- [ ] **P2.4** `TransactionService` layer with `create`, `findAllByUser`, `update`, `delete` methods; DTOs: `TransactionRequest`, `TransactionResponse`, `CategoryResponse`
- [ ] **P2.5** REST API for TransactionV2: `POST`, `GET`, `PUT`, `DELETE /api/v1/transactions`; JWT-secured endpoints with `@PreAuthorize`
- [ ] **P2.6** `CategoryService` + REST API: `POST`, `GET`, `PUT`, `DELETE /api/v1/categories`; prevent deletion if transactions exist
- [ ] **P2.7** Integration tests for auth + CRUD: login flow, transaction CRUD with JWT, 403 validation
- [ ] **P2.8** Monthly summary endpoint: `GET /api/v1/transactions/summary?month=YYYY-MM`; use `Stream` API for grouping/totals
- And more...

**Key Java 17 features used:**
- Records for DTOs (`LoginRequest`, `TransactionRequest`, `TransactionResponse`)
- `Stream.toList()` for mapping/filtering
- `var` in service methods
- Switch expressions in validation logic



### 🕓 Phase 3 — Modernization (planned) !!Needs clarification!!

- Switch expressions & pattern matching in business logic
- Sealed hierarchy for `SpendingType` (`INCOME` / `EXPENSE`)
- Replace remaining POJOs with records where safe
- Modular package refactor & clearer domain boundaries



### 🕓 Phase 4 — Enhancements (planned) !!Needs clarification!!
- [ ] **P1.6** Update Conversion model to UUID PKs
- Currency-rate integration & automatic conversion
- [ ] **P1.5**  ImportBatch entity & FK from Transaction
- CSV import / export, advanced reports, minimal dashboard
- Random test-data generators (`RandomGeneratorFactory`)
- Observability (metrics, tracing) and deployment hardening

---

## Language & Tools

- [JDK17](https://jdk.java.net/archive/)
- [Gradle](https://gradle.org/) - as a package manager
- [Spring Boot](https://spring.io/projects/spring-boot) - as a server-side framework
- [React](https://reactjs.org/) - client-side framework

---

## Quickstart

This section contains all the information required for getting the app up and running. The application contains the following two directories:

- [server/](server/) - the backend (Java 17 + Spring Boot 2.6.6)
- [client/](client/) - the frontend (React)

You can run the stack with Docker (recommended) or run server/client locally in separate terminals.

---

### Docker (recommended)

To run the stack with Docker, follow these steps:

1. From the repo root, build and start the services:

```bash
  docker compose up --build
```

2. Check the backend:
  - Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
  - Logs (persisted): `./logs/mySpendingApp.log`
  - SQLite DB (persisted): `./data/mySpendingApp.db`

3. Open the frontend: [http://localhost:3000](http://localhost:3000)

4. Stop everything:

```bash
  docker compose down
```

---

### Server (local)

To run the server locally (without Docker), follow these steps:

1. Navigate to the server directory (`cd server`)

2. Install/build dependencies:
  - Unix/macOS: `./gradlew build`
  - Windows: `.\gradlew build`

3. Start the server (Tomcat on port 8080):
  - Unix/macOS: `./gradlew bootRun`
  - Windows: `.\gradlew bootRun`

---

### Client (local)

- System requirements
  - NodeJS v18

To run the client locally, follow these steps:

1. Navigate to the client directory (`cd client`)

2. Install dependencies:

```bash
  npm install
```

You can ignore the severity vulnerabilities, this is a [known issue](https://github.com/facebook/create-react-app/issues/11174) related to `create-react-app` and not actual vulnerabilities for this setup.

3. Start the client:

```bash
  npm start
```

---

## Formatting Client

To format your code using [prettier](https://prettier.io/), follow these steps:

1. Navigate to the client directory (`cd client`)

2. Run this command:

```bash
  npm run lint
```

To ensure you are using the correct version of prettier, make sure to format your code after installing the dependencies (`npm install`).

---

## Database and Seed Data

**Note: No manual database setup is required to get started.**

- **Dev (Docker/local)**: the backend uses SQLite, persisted to `./data/mySpendingApp.db` (volume-mounted in Docker). Logs are written to `./logs/mySpendingApp.log`. Data persists across restarts.
- **Seed data**: if the database file is new/empty, Spring may execute `data.sql` on startup to insert demo data. If the DB already exists, existing data is kept and the seed won't reapply.
- **Tests**: run on H2 in-memory via a dedicated `application-test.properties` and do not touch the SQLite file.

Seed files live under `server/src/main/resources/`.


---


## Architecture Decision Records

📄 **[ADR-001: Phase 0 Setup & Phase 1 DB](docs/ADRs/001-Phase0_AND_Phase1-decisions.md)**  
Captures setup and data model decisions (Docker, UUID PKs, test environments, seed strategy).

📄 **[ADR-002: Phase 2 – MVP ✍️](docs/ADRs/002-Phase2-decisions.md)**
Covers SonarQube integration and JWT vs OAuth2/session-based auth decision and more...

---


## Verify That Everything Is Set Up Correctly

To verify that the frontend is working properly, go to [http://localhost:3000](http://localhost:3000). You should see the homepage titled "Welcome to MySpendingApp" and a chart as below.

![Starting Screen](docs/mySpendingApp.png)

---

