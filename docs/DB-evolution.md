# Database Schema Evolution

This document tracks how the data model evolves: what changed, why it changed, and how it impacts persistence across environments (dev vs test). It’s the single source of truth for schema decisions.

---

## 0️⃣ Initial Baseline (end of Phase 0)

**Schema**

| Table          | Columns                                                                 | Notes                                     |
|----------------|-------------------------------------------------------------------------|-------------------------------------------|
| `transactions` | `id PK`, `amount`, `currency`, `date`, `created_at`, `updated_at`       | Single-table prototype in SQLite/H2.      |
| `conversion`   | `id PK (INT)`, `rate (FLOAT)`, `fromCurr (TEXT)`, `toCurr (TEXT)`       | Early FX rates prototype (Phase 0 utility). |

**Notes**
- `conversion` was a lightweight helper to store simple FX rates during Phase 0.
- Ownership/auth not applied yet; relationships arrive in Phase 1+.

---

## 1️⃣ Phase 1 – Model hardening

### 1. Users
- Table: `users` — `id (UUID PK)`, `email`, `password_hash`, `name`, `created_at`.
- Rationale: foundation for authentication (later JWT) and data ownership.
- Uniqueness: enforced at application level on SQLite (DB-level UNIQUE will be enforced on a production RDBMS).

### 2. Categories
- Table: `categories` — `id (UUID PK)`, `name`, `color`, `user_id (FK → users.id)`.
- Constraint: **UK(`user_id`, `name`)** to prevent duplicate names per user.
- Rationale: per-user taxonomy to group spending.

### 3. Transactions (refined)
- `id` migrated to **UUID**.
- Added FKs: `user_id (FK → users.id)`, `category_id (FK → categories.id)`.
- Optional: `note` (short free text).
- Rationale: link each transaction to an owner and a category for meaningful queries and reports.

### 4. Repositories (query surface)
- `UserRepository`: `findByEmail(String email)` as the natural lookup key.
- `TransactionRepository`:
  - `findByUserId(UUID userId)`
  - `findByUserIdAndCategoryId(UUID userId, UUID categoryId)`
  - `getTransactionsForRange(String startDate, String endDate)` *(native)* — supports time-range charts.
  - Optional JPQL: `findByUserEmail(String email)` using a Java 17 text block.

---

## Environments & persistence

### Dev (SQLite, file-backed)
- DB file: `./data/mySpendingApp.db` (volume-mounted).
- Schema: `spring.jpa.hibernate.ddl-auto=update` to evolve without data loss.
- Seed: `data.sql` only applies on **fresh** DB creation; otherwise existing data is kept.

### Test (H2 in-memory)
- Isolated profile (`application-test.properties`):
  - `spring.datasource.url=jdbc:h2:mem:testdb`
  - `spring.jpa.database-platform=org.hibernate.dialect.H2Dialect`
  - `spring.sql.init.mode=never` (no prod seed)
- Tests use `@DataJpaTest` (fast, transactional, rollback after each run).
- Reports: `server/build/reports/tests/test/index.html`.

---

## Current ER snapshot (Phase 1, pre-MVP)

- **users** (UUID PK)
- **categories** (UUID PK) → FK to **users**; **UK(user_id, name)**
- **transactions** (UUID PK) → FKs to **users**, **categories**; optional `note`

This is the base that Phase 2 (MVP) will build on (CRUD, auth, charts).

---

## Conventions

- **Primary keys**: `UUID` stored as `CHAR(36)` (v4, Hibernate 5.6.9.Final), human-readable and cross-DB compatible.
- **Foreign keys**: `UUID` `NOT NULL` unless explicitly optional.
- **Timestamps**: `created_at` / `updated_at` by Hibernate annotations.
- **Uniqueness**: per-user category uniqueness via **UK(user_id, name)**.
- **Email**: treated as logical unique identifier at the application layer on SQLite; DB-level UNIQUE to be enforced in production RDBMS.

---

## Next schema touches (DB-only)
- Seed: demo user + default categories aligned with the refined FKs.
- Repo tests (H2): cover `User`, `Category`, `Transaction` relations and range queries.
- CSV import batches: deferred to a later phase; will add `import_batches` and link to `transactions`.
- FX rates: revisit or remove `conversion` table based on Phase 2+ requirements.
- Production RDBMS: plan migration from SQLite to PostgreSQL or MySQL for production deployment.
- Data migration scripts: if switching RDBMS, plan for data export/import and schema migration scripts.
- Indexing: analyze query patterns and add indexes on frequently queried columns (e.g., `user_id`, `category_id`, `date`).
- Audit logging: consider adding audit tables or triggers to track changes to critical data (e.g., transactions).
- Backup strategy: define a backup and recovery strategy for production databases.
- Performance tuning: monitor and optimize database performance as the application scales.
- Security enhancements: implement encryption for sensitive data and ensure compliance with data protection regulations.
- Documentation updates: keep the schema documentation up-to-date with any changes made during development.
