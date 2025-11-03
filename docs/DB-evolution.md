# Database Schema Evolution

This document tracks how the data model evolves: what changed, why it changed, and how it impacts persistence across environments (dev vs test). It's the single source of truth for schema decisions.

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

## 1️⃣ Phase 1 – Model hardening (Users, Categories, Transactions)

### **P1.1** – Schema baseline documentation
- **What**: Created this evolution log and initial ER diagram.
- **Why**: Establish single source of truth for schema decisions before making structural changes.

---

### **P1.2** – User entity & repository
- **What**: Added `users` table with UUID primary key.
- **Columns**: `id (UUID PK)`, `email`, `password_hash`, `name`, `last_name`, `created_at`.
- **Constraints**: Email uniqueness enforced at application level (SQLite limitation); will use DB-level UNIQUE in production RDBMS.
- **Repository**: `UserRepository` with `findByEmail(String email)` for authentication lookup.
- **Why**: Foundation for user authentication and data ownership. Each transaction and category will belong to a specific user.

---

### **P1.3** – Category entity (per-user)
- **What**: Added `categories` table with UUID primary key.
- **Columns**: `id (UUID PK)`, `name`, `color`, `user_id (FK → users.id)`, `created_at`.
- **Constraints**: 
  - **UK(user_id, name)** prevents duplicate category names per user.
  - `user_id` is NOT NULL (every category must belong to a user).
- **Relationship**: `@ManyToOne` from Category to User.
- **Why**: Users need to organize their spending with custom categories. The unique constraint ensures clean data (no "Food" twice for the same user).

---

### **P1.4** – Dev/Test environment separation
- **What**: Configured H2 in-memory database for tests via `application-test.properties`.
- **Test config**:
  - `spring.datasource.url=jdbc:h2:mem:testdb`
  - `spring.jpa.database-platform=org.hibernate.dialect.H2Dialect`
  - `spring.sql.init.mode=never` (no prod seed data in tests)
- **Dev config**: Remains on SQLite with `ddl-auto=update`.
- **Why**: Isolate test runs from production data. H2 is fast and resets after each test, preventing side effects.

---

### **P1.5** – Transaction relationships & note field
- **What**: Migrated `transactions.id` to UUID and added foreign keys.
- **Changes**:
  - `id`: Changed from auto-increment to **UUID** for consistency.
  - `user_id (FK → users.id)`: Links each transaction to its owner (NOT NULL).
  - `category_id (FK → categories.id)`: Links transaction to a category (NOT NULL).
  - `note (TEXT)`: Optional free-text field for transaction details.
- **Relationships**: `@ManyToOne(fetch = FetchType.LAZY)` to User and Category.
- **Repository queries**:
  - `findByUserId(UUID userId)`: All transactions for a user.
  - `findByUserIdAndCategoryId(UUID userId, UUID categoryId)`: Filter by category.
  - `getTransactionsForRange(String startDate, String endDate)`: Time-range queries for charts.
- **Why**: 
  - UUIDs provide cross-database compatibility and avoid collision in distributed scenarios.
  - Foreign keys ensure data integrity (can't have orphaned transactions).
  - The note field adds context without overcomplicating the schema.
  - Lazy fetching prevents N+1 queries when loading transactions.

---

### P1.6 — Mark conversion as deprecated
- **Status**: Table retained temporarily for frontend chart demo.
- **Rationale**:
  - Frontend currently displays conversion data in chart component.
  - No User/Category/Transaction data in UI yet (Phase 2).
- **Plan**:
  - Remove in Phase 4 when FX rates API is integrated.
  - Will be replaced with real transaction analytics (by category, date range).
- **Code**: Added `@Deprecated` annotation to `Conversion.java`.

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

```
users (UUID PK)
  ├── categories (UUID PK, FK user_id, UK(user_id, name))
  └── transactions (UUID PK, FK user_id, FK category_id)
```

**Key points**:
- All entities use UUID primary keys.
- Foreign keys enforce referential integrity.
- Per-user category uniqueness prevents duplicates.
- Optional `note` field on transactions for user context.

This is the base that Phase 2 (MVP) will build on (CRUD, auth, charts).

---

## Conventions

- **Primary keys**: `UUID` stored as `CHAR(36)` (v4, Hibernate 5.6.9.Final), human-readable and cross-DB compatible.
- **Foreign keys**: `UUID` `NOT NULL` unless explicitly optional.
- **Timestamps**: `created_at` / `updated_at` by Hibernate annotations.
- **Uniqueness**: per-user category uniqueness via **UK(user_id, name)**.
- **Email**: treated as logical unique identifier at the application layer on SQLite; DB-level UNIQUE to be enforced in production RDBMS.

---


## Future considerations

- **Import batches**: Add `import_batches` table when CSV import feature arrives (Phase 4).
- **FX rates**: Revisit `conversion` table design when currency features are implemented.
- **Production RDBMS**: Plan migration from SQLite to PostgreSQL.
- **Indexing**: Add indexes on `user_id`, `category_id`, `date` based on query patterns.
- **Audit logging**: Consider audit tables for transaction history tracking.
