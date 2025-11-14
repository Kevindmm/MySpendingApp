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


## P2.3 – Refresh Token Endpoint

**Date**: 14/11/2025
**Commit**: `feat(refresh token) (#3)`
**Status**: In Progress

### Decision
Implement a refresh token endpoint to extend JWT validity without re-authentication.

### Why
- **Refresh Tokens**: Allows clients to obtain new JWTs using a long-lived refresh token, improving UX by avoiding frequent logins while maintaining security.
- **Stateless Extension**: Builds on existing JWT setup; refresh tokens stored securely (e.g., HttpOnly cookies) mitigate XSS risks.

### Alternatives Considered
- **No Refresh**: Simpler; short-lived JWTs suffice for MVP but poor UX.
- **Session Extension**: Easy with Spring but breaks statelessness; not scalable.
- **OAuth2 Refresh**: Standard; delegates to providers but overkill for local auth.

### Trade-offs
- ✅ **Security**: Refresh tokens can be revoked; short JWT expiry reduces exposure.
- ✅ **Usability**: Seamless token renewal without credentials.
- ⚠️ **Complexity**: Adds refresh token storage/validation logic.
- ⚠️ **Storage**: Refresh tokens need secure, revocable storage (e.g., DB blacklist for MVP).

### Implementation
- **Refresh Endpoint**: `POST /api/v1/auth/refresh` accepts refresh token, validates it, and returns new `LoginResponseDTO` with fresh JWT.
- **JwtTokenProvider**: Extend to generate/validate refresh tokens (longer expiry, e.g., 7 days).
- **SecurityAuthConfig**: Update filter chain to handle refresh tokens securely.
- **Token Structure**: Refresh token payload similar to JWT but with extended expiry.

### What Will Be Done
- Generate/validate refresh tokens.
- Implement endpoint in `AuthController`.


 ---