# ADR-001: Phase 0 & Phase 1 Decisions

This document captures architectural decisions made during the initial setup (Phase 0) and data model hardening (Phase 1). These are historical records of choices already implemented.

---

## Phase 0: Setup

### P0.2: Docker Compose Stack with Persistent Volumes

**Date**: 15-10-2025
**Status**: ✅ Accepted

#### Decision
Use Docker Compose to orchestrate backend (Spring Boot + SQLite) and frontend (React + Nginx) with persistent volumes for database and logs.

#### Consequences
- ✅ Dev/prod parity: same stack runs locally and in CI
- ✅ Data persists across container restarts (`./data/mySpendingApp.db`, `./logs/mySpendingApp.log`)
- ⚠️ SQLite not suitable for high concurrency or production at scale

#### Alternatives
- Embedded H2 → Rejected (no persistence between restarts)
- PostgreSQL in Docker → Deferred to Phase 4 (overkill for MVP)

---

### P0.3: GitHub Actions CI Pipeline

**Date**: 18-10-2025
**Status**: ✅ Accepted

#### Decision
Implement CI workflow with Gradle build, test execution, and build status badge in README.

#### Consequences
- ✅ Automated quality checks on every push/PR
- ✅ Prevents broken builds from merging
- ⚠️ Build time ~2-3 minutes per run

#### Alternatives
- Manual testing → Rejected (not scalable, error-prone)
- Jenkins/GitLab CI → Rejected (GitHub Actions native integration)

---

## Phase 1: Data Model Hardening

### P1.2: UUID Primary Keys

**Date**: 22-10-2025
**Status**: ✅ Accepted

#### Decision
Use UUID (v4) stored as `VARCHAR(36)` for primary keys in `User`, `Category`, and `TransactionV2`.

#### Consequences
- ✅ Cross-database compatibility (SQLite, H2, PostgreSQL)
- ✅ No collision risk in distributed systems or data merges
- ⚠️ Larger index size compared to `BIGINT` (~36 bytes vs 8 bytes)
- ⚠️ Slightly slower joins than integer PKs (marginal for MVP scale)

#### Alternatives
- `BIGINT` auto-increment → Rejected (harder to migrate between databases)
- `BINARY(16)` UUID → Rejected (less human-readable in logs/debugging)

---

### P1.3: Per-User Category Ownership with Unique Constraint

**Date**: 25-10-2025
**Status**: ✅ Accepted

#### Decision
Create `categories` table with:
- `user_id` foreign key (NOT NULL) to `users`
- Composite unique constraint on `(user_id, name)`
- `@ManyToOne` relationship to User

#### Consequences
- ✅ Each user can customize their own category set
- ✅ Prevents duplicate category names per user at DB level
- ⚠️ No global shared categories (e.g., "Food" must be duplicated per user)

#### Alternatives
- Global shared categories → Rejected (less flexible, doesn't scale with custom needs)
- Application-level uniqueness → Rejected (race conditions possible)

---

### P1.4: Separate Test Environment with H2 In-Memory

**Date**: 28-10-2025
**Status**: ✅ Accepted

#### Decision
Use H2 in-memory database for tests (`application-test.properties`) and SQLite for dev/Docker.

#### Consequences
- ✅ Fast, isolated test runs (~10-20ms per test)
- ✅ No side effects on dev database (`./data/mySpendingApp.db`)
- ⚠️ Requires separate seed file (`data-h2.sql`) to align with SQLite schema

#### Alternatives
- Share SQLite for tests → Rejected (tests pollute dev data, slower)
- Testcontainers with PostgreSQL → Rejected (overkill for MVP, slower CI)

---

### P1.8: TransactionV2 as MVP Entity

**Date**: 01-11-2025
**Status**: ✅ Accepted

#### Decision
Create `TransactionV2` entity with:
- UUID primary key
- Foreign keys to `User` and `Category` (NOT NULL)
- `@ManyToOne(fetch = LAZY)` relationships
- Legacy `Transaction` (Long PK) kept only for chart until Phase 4

#### Consequences
- ✅ Clean referential integrity with FK constraints
- ✅ Lazy fetching prevents N+1 queries
- ⚠️ Two transaction tables until Phase 4 chart migration
- ⚠️ Requires explicit `@Table(name = "transactionsv2")` to avoid naming conflict

#### Alternatives
- Migrate chart immediately → Rejected (out of scope for Phase 1)
- Keep single `Transaction` with nullable FKs → Rejected (violates data integrity)

---

### P1.10: UUID VARCHAR(36) Mapping for H2 Compatibility

**Date**: 03-11-2025
**Status**: ✅ Accepted

#### Decision
Add `@Type(type = "org.hibernate.type.UUIDCharType")` to all UUID fields in `User`, `Category`, and `TransactionV2`.

#### Consequences
- ✅ UUIDs persist and query correctly as `VARCHAR(36)` in both H2 and SQLite
- ✅ Fixes H2 hexadecimal conversion bug (was returning UUIDs as `0x...` strings)
- ⚠️ Hibernate 5.6.9.Final required for `UUIDCharType` support

#### Alternatives
- Custom `AttributeConverter` → Rejected (more boilerplate, same result)
- Keep H2 default UUID type → Rejected (incompatible with SQLite `CHAR(36)`)

---

### P1.9 & P1.10: Seed Data Strategy

**Date**: 04-11-2025
**Status**: ✅ Accepted

#### Decision
Maintain separate seed files:
- `data.sql` for SQLite dev/Docker (demo user, 3 categories, 5 transactions)
- `data-h2.sql` for H2 test environment (same structure, different syntax)

#### Consequences
- ✅ Realistic demo data for frontend development
- ✅ Tests run with consistent seed data
- ⚠️ Must keep both files in sync manually

#### Alternatives
- Single seed file with conditional SQL → Rejected (fragile, hard to maintain)
- No seed data → Rejected (manual setup required for every fresh DB)

---

## Summary

| Decision | Rationale | Trade-off |
|----------|-----------|-----------|
| Docker Compose | Dev/prod parity, portable stack | SQLite not prod-ready |
| UUID PKs | Cross-DB compatibility | Larger indexes |
| Per-user categories | Customization, data isolation | No shared global categories |
| H2 for tests | Fast, isolated runs | Separate seed file needed |
| TransactionV2 MVP | Clean FK constraints | Two tables until Phase 4 |
| UUID VARCHAR(36) | H2/SQLite compatibility | Requires explicit Hibernate type |
