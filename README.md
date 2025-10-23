# MySpendingApp

**MySpendingApp** is a lightweight personal finance tracker built with **Java 17** and **Spring Boot**.\
The goal is to create a clear and maintainable backend while showcasing modern Java 17 features in a practical way.

---

## 🚀 Development Roadmap

| Phase | Purpose | Status            |
|-------|---------|-------------------|
| **Phase 0 – Setup** | Project scaffold, health check, CI | ✍️ Working on it! |
| **Phase 1 – MVP (Spending CRUD)** | First usable backend with Java 17 essentials | ⬜ Planned         |
| **Phase 2 – Modernization** | Replace remaining legacy constructs with Java 17 features | ⬜ Planned         |
| **Phase 3 – Enhancements** | Currency API, reports, dashboards, test-data tools | ⬜ Planned         |



### ✍️ Phase 0 — Setup (Working on it!)

- Spring Boot skeleton + Gradle build ✅
- `/actuator/health` endpoint exposed and **healthy** ✅
- GitHub Actions workflow (build + tests badge) ✅
- Dockerised stack: **backend (JDK 17 + SQLite) & frontend (React + Nginx) run via `docker compose`** ✅
- Fix persistent logging: mount `./logs → /app/logs` and set `logging.file.path=/app/logs` ✍️ Working on it!
- Local DB baseline: **SQLite** (dev) / **H2** (tests) ✍️ Working on it!
- Quick filters & sorting (date, category) using `LocalDate` + Streams
- Seed data + smoke tests



### ⬜ Phase 1 — MVP (Spending CRUD)

**Goal:** deliver core CRUD + summary endpoints using Java 17 records and Streams.

| Item | Detail |
|------|--------|
| **Entity** | `Spending` (`id`, `amount`, `currency`, `category`, `type`, `date`) |
| **Endpoints** | `POST /api/v1/spendings` · `GET /api/v1/spendings` · `GET /api/v1/spendings/summary` |
| **Persistence** | SQLite (prod dev) / H2 (tests) |
| **Key Java 17** | Records (`SpendingRequest`, `SpendingResponse`) · `Stream.toList()` · `var` · Jakarta Validation |



#### Task Checklist
- [x] Bootstrap project
- [ ] Spending entity
- [ ] Record DTOs
- [ ] Repository (`JpaRepository<Spending, Long>`)
- [ ] Service (`create`, `findAll`, `summary`)
- [ ] REST controller + MockMvc tests
- [ ] Validation on DTOs
- [ ] SQLite config (`ddl-auto=update`)
- [ ] Integration tests (happy path)



### 🕓 Phase 2 — Modernization (planned)

- Switch expressions & pattern matching in business logic
- Sealed hierarchy for `SpendingType` (`INCOME` / `EXPENSE`)
- Replace remaining POJOs with records where safe
- Modular package refactor & clearer domain boundaries



### 🕓 Phase 3 — Enhancements (planned)

- Currency-rate integration & automatic conversion
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

This section contains all the information required for getting the server up and running. The application contains the following two directories:

- [server/](server/) - the server directory that contains the server code
- [client/](client/) - the client directory that contains the client code

In order to run the application, you will need to have both the server and client running in separate terminals.

---

### Server

To run the server, follow these steps:

1. Navigate to the server directory (in Unix that would be `cd server`)

2. Install the required dependencies by running `gradle build` or `gradlew build` if you're in Windows.

3. Start the server. `gradle bootRun` or `gradlew bootRun` - launches the Tomcat Server in debug mode on port 8080.

---

### Client

- System requirements
  - NodeJS v18

To run the client, follow these steps:

1. Navigate to the client directory (in Unix that would be `cd client`)

2. Install dependencies

```bash
npm install
```

You can ignore the severity vulnerabilities, this is a [known issue](https://github.com/facebook/create-react-app/issues/11174) related to `create-react-app` and are not real vulnerabilities.

3. Start the client

```bash
npm start
```

---

## Formatting Client

To format your code using [prettier](https://prettier.io/), follow these steps:

1. Navigate to the client directory (in Unix that would be `cd client`)

2. Run this command:

```bash
npm run lint
```

To ensure you are using the correct version of prettier, make sure to format your code after installing the dependencies (`npm install`).

---

## Database and Seed Data

**Note: No database setup should be required to get started with running the project.**

This project uses SQLite, which stores your tables inside a file (`server/database.db`) for development. You can inspect the seed data by looking into the (`server/src/main/resources`) folder in the main package.

The database is seeding from the `data.sql` file everytime the application is running. The data is seeded relative to today's date.

---

## Verify That Everything Is Set Up Correctly

To verify that the frontend is working properly, go to [http://localhost:3000](http://localhost:3000). You should see the homepage that is titled "Welcome to My Spending App" and a chart as below.

![Starting Screen](https://storage.googleapis.com/m.hatchways.io/SpendingApp-screenshot.png)

---

