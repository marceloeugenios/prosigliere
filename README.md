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
- Docker (used to run Redis via `docker-compose.yaml`)
- MySQL

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

### Auth & security
- **OAuth2 + JWT (Resource Server)** — replace the current API key auth with OAuth2. A dedicated/company authorization server (e.g. Keycloak or Spring Authorization Server) issues signed JWTs on login; the API validates the signature and extracts the user identity from the JWT claims. The `author` UUID on posts and comments would then be populated from the JWT subject instead of the request body, removing the current trust issue where any caller can claim any author identity. This also enables role-based access (e.g. only the post author can delete it) and token scopes without changing the API contract.
- Input sanitisation (HTML escaping)

### Entity enhancements
- **Categories** — many-to-many between `blog_posts` and a new `categories` table (id, name, slug). Enables filtering posts by category (`GET /api/v1/posts?category=tech`) and grouping in the list response. A post can belong to multiple categories.
- **Tags** — similar to categories but lightweight and user-defined; many-to-many via a `post_tags` join table. Useful for freeform labelling without a curated taxonomy.
- **Post status** — add a `status` column (`DRAFT`, `PUBLISHED`, `ARCHIVED`) to `blog_posts`. Only `PUBLISHED` posts appear in the public list; authors can retrieve their own drafts. Pairs naturally with the OAuth2 work above since ownership needs to be enforced.
- **Nested comments (replies)** — self-referential FK `parent_id` on `comments` pointing back to the same table. Enables one level of threaded replies without a separate entity.
- **Reactions** — a `post_reactions` table (post_id, author UUID, type enum: `LIKE`, `LOVE`, …) with a unique constraint on `(post_id, author)`. Returns an aggregated count per type on the post detail response.

### API & infrastructure
- Photo and file attachments for posts (S3-compatible storage, multipart upload endpoint)
- Pagination sorting by any column via `sort` and `direction` query parameters and improve `Pageable` handling in the controller
- Soft deletes for posts and comments, or a status field with active/inactive states and a scheduled job to hard-delete old inactive records
- Metrics are already being generated, so next step could be using Prometheus + Grafana

### CI/CD (GitHub Actions)
- **CI workflow** (`ci.yml`) — triggered on every push and pull request to `main`: run `./gradlew spotlessCheck` (format gate), `./gradlew test` (Testcontainers requires Docker on the runner, use `ubuntu-latest`), and upload the JaCoCo report as an artifact. Fail fast on format or coverage violations before any review.
- **Release workflow** (`release.yml`) — triggered on push of a version tag (`v*.*.*`): run CI, then `./gradlew bootJar`, build and push the Docker image to Docker Hub (or GHCR) tagged with the Git tag and `latest`, and create a GitHub Release with the JAR as an attached asset.
- **Dependabot** — enable `gradle` and `docker` ecosystems in `.github/dependabot.yml` to get automated PRs for dependency upgrades. Pair with the CI workflow so each Dependabot PR is validated automatically.
- **Branch protection** — require the CI workflow to pass before merging to `main`; enforce at least one review. This makes the format and coverage gates mandatory rather than advisory.
- **Secret management** — store `DOCKER_USERNAME`, `DOCKER_PASSWORD` (or `GITHUB_TOKEN` for GHCR), and `API_BLOG_TOKEN` as GitHub Actions secrets. Never embed credentials in workflow YAML.
