# Blog API

A RESTful API for managing blog posts and comments.

## Tech stack

| Layer | Technology |
|---|---|
| Runtime | Java 25, Spring Boot 4.0, Virtual Threads |
| Persistence | MySQL 8, Spring Data JPA, Hibernate, Flyway |
| Caching | Redis, Spring Cache |
| Mapping | MapStruct |
| Auth | API key (`StaticTokenAuthFilter`) + HTTP Basic (Swagger) |
| Observability | Micrometer, Prometheus |
| Docs | SpringDoc OpenAPI 3 |
| Testing | JUnit 5, Mockito, Testcontainers |
| Build | Gradle, Spotless (Google Java Format), JaCoCo |

## Development approach

This project was developed using **[Claude Code](https://claude.ai/code)** (Anthropic's AI CLI) with the **[Superpowers](https://github.com/anthropics/superpowers)** plugin. The workflow involved agentic plan execution. All driven through AI-assisted development.

## Evaluator quickstart

No JDK, Mysql or Redis required — just Docker.

```bash

docker compose -f docker/docker-compose-cloud.yml up -d

```

This starts the published application image together with MySQL and Redis. Once the containers are healthy, open Swagger UI:

```
http://localhost:8080/blog/swagger-ui/index.html
```

The Swagger UI is protected by HTTP Basic Auth using shared static credentials configured in `application.yml`:
- Username: `challenge`
- Password: `challenge`

Then authorize the API token via the **Authorize** button so requests go through:

```
API-BLOG: LKUBv5oNJ8D2hs3LMy0yemrkeEu6bm4SUGryjjr/5o8=
```

All endpoints have example values pre-filled — hit **Try it out** on any operation to start testing.

## Running locally

### Prerequisites

- JDK 25
- Docker

Flyway runs migrations automatically on startup. Swagger UI is available at:

`http://localhost:8080/blog/swagger-ui/index.html` (requires Basic Auth: `challenge` / `challenge`)

## Authentication

The API uses **API key authentication**. Every request must include the token as a custom header:

```
API-BLOG: <token>
```

The token is configured via the `API_BLOG_TOKEN` environment variable. When running locally, the default fallback token is:

```
LKUBv5oNJ8D2hs3LMy0yemrkeEu6bm4SUGryjjr/5o8=
```

## Endpoints

Base path: `/blog`

| Method | Path | Description | Success |
|--------|------|-------------|---------|
| `GET` | `/api/v1/posts` | List posts (paginated, with comment count) | `200` |
| `POST` | `/api/v1/posts` | Create a post | `201` |
| `GET` | `/api/v1/posts/{id}` | Get post with comments | `200` |
| `POST` | `/api/v1/posts/{postId}/comments` | Add a comment to a post | `201` |

### Query parameters — GET /api/v1/posts

| Parameter | Default | Description |
|-----------|---------|-------------|
| `page` | `0`     | Page number (zero-based) |
| `size` | `10`    | Page size |

### Request bodies

**POST /api/v1/posts**
```json
{
  "title": "My first post",
  "content": "Hello, world!",
  "author": "01963f5e-4a00-7e8b-a3c2-53f98c7b1234"
}
```

**POST /api/v1/posts/{postId}/comments**
```json
{
  "content": "Great post!",
  "author": "01963f5e-4a00-7e8b-a3c2-53f98c7b1234"
}
```

> `author` is a UUID referencing the user who created the content.

## Running tests

```bash
# Format code (required before tests pass CI)
./gradlew spotlessApply

# Run tests (Docker must be running — Testcontainers starts MySQL and Redis automatically)
./gradlew test

# Full build: formatting check + tests + JaCoCo coverage report
./gradlew build
```

## Next steps

- **OAuth2 + JWT (Resource Server)** — replace the current API key auth with OAuth2. A dedicated/company authorization server (e.g. Keycloak or Spring Authorization Server) issues signed JWTs on login; the API validates the signature and extracts the user identity from the JWT claims. The `author` UUID on posts and comments would then be populated from the JWT subject instead of the request body, removing the current trust issue where any caller can claim any author identity. This also enables role-based access (e.g. only the post author can delete it) and token scopes without changing the API contract.
- Photo and file attachments for posts (S3-compatible storage, multipart upload endpoint)
- Pagination sorting by any column via `sort` and `direction` query parameters and improve `Pageable` handling in the controller
- Soft deletes for posts and comments
- Input sanitisation (HTML escaping)
- Metrics are already being generated, so next step could be using Prometheus + Grafana
- CI/CD pipeline with GitHub Actions (build, test, publish image on merge to main)
