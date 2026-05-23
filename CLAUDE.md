# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Format code (must pass before CI)
./gradlew spotlessApply

# Run all tests (Docker must be running — Testcontainers starts MySQL + Redis automatically)
./gradlew test

# Run a single test class
./gradlew test --tests "br.com.challenge.controller.BlogPostControllerTest"

# Full build: format check + tests + JaCoCo coverage report (90% minimum)
./gradlew build

# Start local Redis only (MySQL must be provided separately)
docker compose -f docker-compose.yaml up -d

# Start full stack using published image (no JDK required)
docker compose -f docker/docker-compose-cloud.yml up -d
```

## Architecture

Standard Spring Boot layered architecture: `controller → service → repository → domain`.

**Request flow:**
1. `StaticTokenAuthFilter` validates the `API-BLOG` header token before any controller is reached. Swagger UI (`/swagger-ui/**`, `/v3/api-docs/**`) uses HTTP Basic Auth instead.
2. Controllers map request DTOs → domain objects via MapStruct mappers, call the service, then map the result back to response DTOs.
3. `BlogPostServiceImpl` applies `@Cacheable`/`@CacheEvict` on `findById`/`saveComment` using the Redis cache named `blog-post-cache-v1` (10-minute TTL). `findAll` (list) is not cached.
4. `BlogPostRepository.fetchById` uses a JPQL `LEFT JOIN FETCH` to eagerly load comments in one query, avoiding N+1.

**Key design decisions:**
- Virtual threads enabled globally (`spring.threads.virtual.enabled=true`).
- Schema managed by Flyway; `ddl-auto: none` — never let Hibernate touch the schema.
- JaCoCo enforces 90% coverage; config, DTOs, mappers, and exceptions are excluded from the measurement.
- `author` on posts and comments is a raw UUID supplied by the caller — there is no user table or ownership enforcement yet (OAuth2/JWT is a noted next step).

## Profiles

| Profile | Used for | Notes |
|---------|----------|-------|
| `local` | Local dev with JDK | Requires MySQL on port 3306, Redis on port 16340 (see `docker-compose.yaml`) |
| `cloud` | Default / production | All infra config supplied via env vars |

Tests activate the `local` profile and override datasource/Redis via `TestContainerConfig`.

## Authentication

- **API endpoints** — `API-BLOG: <token>` header. Default dev token: `LKUBv5oNJ8D2hs3LMy0yemrkeEu6bm4SUGryjjr/5o8=` (env var `API_BLOG_TOKEN`).
- **Swagger UI** — HTTP Basic: `challenge` / `challenge`.
- Actuator (`/actuator/**`, `/health/**`) is open with no auth.

## Testing conventions

All integration tests extend `BaseTest`, which wires `MockMvc` and bootstraps Testcontainers (MySQL + Redis) via `TestContainerConfig`. Use object mothers in `objectmother/` to build request DTOs. Unit tests for the service layer (`BlogPostServiceImplTest`) use Mockito and do not extend `BaseTest`.
