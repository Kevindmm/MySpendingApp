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

**Date**: 13/11/2025  
**Commit**: `P2.2 - add JWT authentication and config _ feat(authentication) (#2)`
**Status**: Implemented (with minor ADR alignment needed)

### Decision
Use **JWT (JSON Web Tokens)** for stateless authentication instead of OAuth2 or session-based auth. Algorithm automatically selects **HS384 (HMAC-SHA384)** if secret key ≥48 bytes (384 bits), providing stronger security than HS256.

### Why
- **Stateless**: No server-side session storage required; scales horizontally.
- **Self-contained**: Token includes claims; no DB lookup on every request.
- **Standard**: Industry-proven, well-supported by Spring Security (`JwtTokenProvider`, `JwtAuthenticationFilter`).
- **Simplicity**: For a single-user MVP, JWT avoids OAuth2's complexity.

### Alternatives Considered

| Approach              | Pros                                      | Cons                                                  |
|-----------------------|-------------------------------------------|-------------------------------------------------------|
| **Session-based**     | Simple; built into Spring                 | Requires session storage; doesn't scale horizontally  |
| **OAuth2 (e.g., Google)** | Delegates auth; social login          | External dependency; overkill for local-first app     |
| **Basic Auth**        | Zero setup                                | Insecure over HTTP; no token expiration               |

## Trade-offs
- ✅ **Scalability**: Stateless tokens work across distributed backends (future-proof).
- ✅ **Security**: HMAC-signed tokens prevent tampering; short-lived (1h expiry in Prod env) reduces risk. HS384 selected automatically for stronger hashing if key allows.
- ⚠️ **Token revocation**: No built-in invalidation (acceptable for MVP; future: add refresh tokens + blacklist).
- ⚠️ **XSS risk**: Storing JWT in `localStorage` exposes it to XSS (mitigated by `HttpOnly` cookies in Phase 3).

### Implementation
- **`AuthController`**: `POST /api/v1/auth/login` returns `LoginResponseDTO` (token + username/email) on valid credentials.
- **`JwtTokenProvider`**: Generates/validates tokens (secret key from `application.properties`; enforces ≥32 bytes, auto-selects HS384 if ≥48 bytes).
- **`SecurityAuthConfig`**: Configures filter chain to extract JWT from `Authorization: Bearer <token>`.
- **`JwtAuthenticationFilter`**: Processes JWT in requests.
- **Token structure** (JWT payload, returned in `LoginResponseDTO.token` - client decodes to access):
```Token payload - json
  {
    "sub": "john.doe@example.com",
    "iat": 1763146656,
    "exp": 1763506656
  }
```

> **Note**: Currently uses email as `sub` for usability (tradeoff vs. privacy). In Phase 3, refactor to use user UUID as `sub` and add `email` and `roles` claims.


---


## P2.3 – Refresh Token & Logout

**Date**: 27/01/2026
**Commit**: `feat(refresh token) - Hybrid approach with stateful refresh tokens`
**Status**: In progress

### Decision
Implement **Hybrid Token System**: stateless JWT access tokens (100h) + stateful refresh tokens (7d) stored in database. Enables token revocation while maintaining scalability.

### Why
- **Revocation**: Stateful refresh tokens can be invalidated immediately (logout, security breach).
- **Scalability**: Access tokens remain stateless; only refresh operations hit DB.
- **Security**: Short-lived access tokens limit exposure; refresh tokens enable seamless renewal.
- **Auditability**: Database storage enables session tracking and security monitoring.

### Alternatives Considered

| Approach              | Pros                                      | Cons                                                  |
|-----------------------|-------------------------------------------|-------------------------------------------------------|
| **Pure Stateless**    | Maximum scalability; zero DB overhead     | Cannot revoke tokens; logout is client-side only      |
| **Pure Stateful**     | Easy revocation; built-in Spring support  | Requires session storage; breaks horizontal scaling   |
| **Hybrid (chosen)**   | Revocable + scalable                      | Slightly more complex implementation                  |
| **OAuth2 Refresh**    | Industry standard                         | Overkill for local auth; external dependencies        |

### Trade-offs
- ✅ **Security**: Refresh tokens revocable; access tokens short-lived; database tracks revocation.
- ✅ **Usability**: Seamless renewal without re-authentication; 7-day refresh window.
- ✅ **Performance**: Most requests stateless; only refresh operations query DB.
- ⚠️ **Complexity**: Requires entity, repository, service layer, and validation logic.
- ⚠️ **Storage**: Refresh tokens in DB; cleanup strategy needed (see DB-evolution.md).

### Implementation
- **`RefreshToken` entity**: Stores token, user, expiry, revocation status (schema in DB-evolution.md).
- **`RefreshTokenService`**: Business logic for create, validate, revoke, cleanup.
- **`AuthController`**:
  - `POST /api/auth/login`: Returns both access + refresh tokens; saves refresh to DB.
  - `POST /api/auth/refresh`: Validates refresh token (JWT + DB + expiry + revoked), returns new access token.
  - `POST /api/auth/logout`: Revokes refresh token in DB.
- **`JwtTokenProvider`**: Extended to generate/validate refresh tokens with `"type":"refresh"` claim.
- **DTOs**: `RefreshTokenRequestDTO`, `RefreshTokenResponseDTO`, updated `LoginResponseDTO`.
- **Token structure**:
  ```json
  // Access Token (stateless, 100h)
  { "sub": "user@example.com", "iat": 1769511332, "exp": 1769871332 }
  
  // Refresh Token (stateful, 7d)
  { "sub": "user@example.com", "type": "refresh", "iat": 1769511332, "exp": 1770116132 }
  ```

### Testing
- New tests added, all passing.
- Coverage: JwtTokenProvider, RefreshTokenService, AuthController, DTOs, entity.
- H2 in-memory DB for tests (see application-test.properties).

### Future Enhancements
- Device tracking (user agent, IP) for session management.
- Automatic cleanup job for expired tokens.
- HttpOnly cookies for refresh token storage (XSS mitigation).
- Rate limiting for refresh endpoint.

---