# Database Schema Evolution

Tracks what changed, why, and how it impacts dev/test environments.

---

## 0️⃣ Initial Baseline (end of Phase 0)

**Schema**

| Table          | Columns                                                   | Notes                     |
|----------------|-----------------------------------------------------------|---------------------------|
| `transactions` | `id PK`, `amount`, `currency`, `date`, timestamps         | SQLite prototype          |
| `conversion`   | `id PK`, `rate`, `fromCurr`, `toCurr`                     | FX rates helper           |

No auth/ownership yet.

---

## 1️⃣ Phase 1 – Model hardening (Users, Categories, TransactionsV2)

### P1.1 – Schema baseline documentation
- **What**: Created this evolution log + ER diagram.
- **Why**: Establish single source of truth before structural changes.

---

### P1.2 – User entity & repository
- **What**: Added `users` table with UUID PK.
- **Columns**: `id (UUID)`, `email`, `password_hash`, `name`, `last_name`, `created_at`.
- **Constraints**: Email uniqueness at app level (SQLite limitation).
- **Repository**: `UserRepository` with `findByEmail(String)`.
- **Why**: Foundation for auth and data ownership.

---

### P1.3 – Category entity (per-user)
- **What**: Added `categories` table with UUID PK.
- **Columns**: `id (UUID)`, `name`, `color`, `user_id (FK → users)`, `created_at`.
- **Constraints**: 
  - **UK(user_id, name)** prevents duplicate categories per user.
  - `user_id` NOT NULL.
- **Relationship**: `@ManyToOne` to User.
- **Why**: Custom spending categories with clean data integrity.

---

### P1.4 – Dev/Test environment separation
- **What**: Configured H2 in-memory for tests via `application-test.properties`.
- **Test config**: 
  - `jdbc:h2:mem:testdb`, no seed data.
  - Fast, isolated runs.
- **Dev config**: SQLite with `ddl-auto=update`.
- **Why**: Prevent test side effects, keep prod data safe.

---

### P1.5 – Transaction relationships & UUID migration
- **What**: Migrated `transactions.id` to UUID and added FK relationships.
- **Changes**:
  - `id`: Long → **UUID**.
  - Added `user_id (FK → users, NOT NULL)`.
  - Added `category_id (FK → categories, NOT NULL)`.
- **Relationships**: `@ManyToOne(fetch = LAZY)` to User and Category.
- **Repository queries**:
  - `findByUserId(UUID)`
  - `findByUserIdAndCategoryId(UUID, UUID)`
  - `getTransactionsForRange(String, String)`
- **Why**: 
  - UUIDs for cross-DB compatibility.
  - FK constraints ensure data integrity.
  - Lazy fetching prevents N+1 queries.

---

### P1.6 – Mark `conversion` deprecated
- **What**: Marked `conversion` table as deprecated, kept for chart demo.
- **Why**: Legacy FX helper; will be removed/refined in Phase 4 with proper FX integration.

---

### P1.7 – Add `note` field
- **What**: Added optional `note (TEXT, 255)` column to transactions.
- **Why**: Allows user context ("Dinner with friends") without schema complexity.

---

### P1.8 – TransactionV2 (MVP entity)
- **What**: Created `TransactionV2` as **main entity for MVP** (Phase 2+).
- **Structure**: 
  - UUID PK, `user_id (FK)`, `category_id (FK)`.
  - `amount`, `currency`, `date`, `note`, timestamps.
  - `@ManyToOne(LAZY)` to User and Category.
- **Legacy handling**: 
  - Old `Transaction` (Long PK, no FK) **kept only for chart** until Phase 4.
  - `TransactionV2` maps to `transactionsv2` table.
- **Bug fixes**: Table name collision, relationship mappings.
- **Why**: All MVP development uses TransactionV2; chart migration deferred to Phase 4.

---

### P1.9 – Update `data.sql` with UUIDs + seed demo user, categories & transactionsv2
- **What**: Migrated `data.sql` to use UUID primary keys and added seed data for demo user, categories, and transactions.
- **Why**: Provide realistic demo data for frontend development and enable environment-specific configurations.

---

### P1.10 – H2 integration tests + UUID VARCHAR(36) migration
- **What**:
  - Added `@Type(type = "org.hibernate.type.UUIDCharType")` to UUID fields in `User`, `Category`, and `TransactionV2`.
  - Created `data-h2.sql` seed file for H2 test environment (separate from SQLite `data.sql`).
  - 27 integration tests for `UserRepository`, `CategoryRepository`, and `TransactionRepositoryV2`.
- **Why**: H2 was converting UUIDs to hexadecimal during queries, causing mismatches. Forcing `VARCHAR(36)` mapping fixed compatibility.
- **Result**: UUIDs now persist and query correctly as `VARCHAR(36)` in both H2 and SQLite.


---


## Environments

### Dev (SQLite)
- File: `./data/mySpendingApp.db`.
- `ddl-auto=update`, `data.sql` on fresh DB only.

### Test (H2 in-memory)
- `jdbc:h2:mem:testdb`, no seed data.
- Reports: `server/build/reports/tests/test/index.html`.

---

## Current ER snapshot (Phase 1, pre-MVP)

```
users (UUID PK)
  ├── categories (UUID PK, FK user_id, UK(user_id, name))
  └── transactionsv2 (UUID PK, FK user_id, FK category_id) ← MVP

[DEPRECATED]
transactions (LONG PK) — chart only, removed Phase 4
conversion (INT PK) — FX demo, removed Phase 4
```

---

## Conventions

- **Primary keys**: `UUID` stored as `CHAR(36)` (v4, Hibernate 5.6.9.Final), human-readable and cross-DB compatible.
- **Foreign keys**: `UUID` `NOT NULL` unless explicitly optional.
- **Timestamps**: `created_at` / `updated_at` by Hibernate annotations.
- **Uniqueness**: per-user category uniqueness via **UK(user_id, name)**.
- **Email**: treated as logical unique identifier at the application layer on SQLite; DB-level UNIQUE to be enforced in production RDBMS.

---

## Future changes (Phase 4+)

- Chart migration to TransactionV2 API (Phase 4).
- Remove legacy `transactions` + `conversion` tables (Phase 4).
- Add indexes on `user_id`, `category_id`, `date`.
- PostgreSQL migration plan.
