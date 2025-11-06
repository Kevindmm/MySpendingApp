# ADR-002: Phase 2 – MVP Authentication & Quality Gates

**Status**: In Progress
**Date**: 05/11/2025
**Context**: Phase 2 focuses on implementing authentication and core CRUD operations for the MVP, building on the mature schema from Phase 1.

---

## P2.1 – SonarQube Integration

**Date**: 05/11/2025
**Commit**: `Update readme with SonarQube badge` + `feat(SonarQube) (#1)`

### Decision
Integrated **SonarCloud** with GitHub Actions to enforce code quality gates.

### Why
- **Automated quality checks**: Detects bugs, code smells, and security vulnerabilities on every commit.
- **No local setup**: Cloud-based, zero infrastructure overhead.
- **Public visibility**: Badge in README shows project health at a glance.
- **CI/CD integration**: Fails builds if quality gate thresholds are breached.

### Alternatives Considered
- **SonarQube self-hosted**: Requires server maintenance, overkill for a single-developer project.
- **Manual code reviews**: Not scalable; quality gate automation is faster and consistent.

### Trade-offs
- ✅ Free for open-source projects.
- ✅ Continuous feedback loop (PR checks).
- ⚠️ Adds \~30s to CI pipeline (acceptable for quality gains).

---

## P2.2 – JWT Authentication

**Date**: 05/11/2025
**Being Implemented**

### Decision
Use **JWT (JSON Web Tokens)** for stateless authentication instead of OAuth2 or session-based auth.

### Why
- **Stateless**: No server-side session storage required; scales horizontally.
- **Self-contained**: Token includes `userId`, `email`, `roles`; no DB lookup on every request.
- **Standard**: Industry-proven, well-supported by Spring Security (`@PreAuthorize`, `JwtTokenProvider`).
- **Simplicity**: For a single-user MVP, JWT avoids OAuth2's complexity (authorization servers, client credentials, refresh token flows).

### Alternatives Considered

| Approach              | Pros                                      | Cons                                                  |
|-----------------------|-------------------------------------------|-------------------------------------------------------|
| **Session-based**     | Simple; built into Spring                 | Requires session storage; doesn't scale horizontally  |
| **OAuth2 (e.g., Google)** | Delegates auth; social login          | External dependency; overkill for local-first app     |
| **Basic Auth**        | Zero setup                                | Insecure over HTTP; no token expiration               |

### Trade-offs
- ✅ **Scalability**: Stateless tokens work across distributed backends (future-proof).
- ✅ **Security**: HMAC-signed tokens prevent tampering; short-lived (1h expiry) reduces risk.
- ⚠️ **Token revocation**: No built-in invalidation (acceptable for MVP; future: add refresh tokens + blacklist).
- ⚠️ **XSS risk**: Storing JWT in `localStorage` exposes it to XSS (mitigated by `HttpOnly` cookies in Phase 3).

### Implementation
- **`AuthController`**: `POST /api/v1/auth/login` returns JWT on valid credentials.
- **`JwtTokenProvider`**: Generates/validates tokens (secret key from `application.properties`).
- **`SecurityConfig`**: Configures filter chain to extract JWT from `Authorization: Bearer <token>`.
- **Token structure**:
  ```json
  {
    "sub": "user-uuid",
    "email": "demo@example.com",
    "iat": 1704067200,
    "exp": 1704070800
  }
