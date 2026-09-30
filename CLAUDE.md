# Recipe Manager

Multi-user recipe manager: React SPA + Spring Boot REST API + PostgreSQL, all run with Docker Compose.

## Source of truth

- `docs/PRD.md` holds the requirements. Requirement IDs such as `REC-1` and `SOC-5` are used everywhere (tickets, commits, tests).
- `docs/TECH_SPEC.md` holds the architecture, data model, API, and conventions.
- If code and docs disagree, stop and ask. Do not quietly change either one. Use the `sync-docs` skill when a change to the docs is agreed.

## Layout

```
backend/    Spring Boot 3, Java 21, Maven. Package root com.recipemanager
frontend/   React 18 + TypeScript + Vite
docs/       PRD, Tech Spec
docker-compose.yml, .env.example
```

## Commands

| Task | Command |
|---|---|
| Backend tests | `cd backend && ./mvnw -B verify` |
| Frontend lint/types/tests | `cd frontend && npm run lint && npm run typecheck && npm test -- --run` |
| Frontend build | `cd frontend && npm run build` |
| Whole stack | `docker compose up --build` (frontend :3000, backend :8080, Swagger `/swagger-ui.html`) |

The `verify` skill runs all of these and reports the results.

## Conventions

- **Tickets** are GitHub Issues. Each ticket lists the PRD requirement IDs it covers and has acceptance criteria as a checklist.
- **Branches:** `<type>/<issue#>-<short-slug>`, e.g. `feat/12-recipe-crud`. Types: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`.
- **Commits:** Conventional Commits, e.g. `feat(recipe): add fork endpoint (#12)`. Mention requirement IDs in the body when relevant.
- **PRs:** one ticket per PR, using `.github/pull_request_template.md`. The body includes `Closes #<issue>`. Never push directly to `main` for feature work.
- **Backend:**
  - Code flows Controller → Service → Repository. Controllers only accept and return DTOs (Java records). Entities never leave the service layer.
  - Authorization checks go in services. Every endpoint has an integration test (MockMvc + Testcontainers).
  - Errors are returned as `ProblemDetail`.
  - A private recipe the caller can't see returns **404**, not 403.
- **DB:** schema changes go only through Flyway migrations in `backend/src/main/resources/db/migration`. Never edit a migration that has already been merged. See the `add-migration` skill.
- **Frontend:**
  - Server state uses TanStack Query.
  - Browse filter state lives in the URL.
  - Forms use React Hook Form + Zod, with rules that mirror server validation.
  - Styling is mobile-first Tailwind. Never use `dangerouslySetInnerHTML`.
- **Tests:** every requirement ID a ticket touches is covered by at least one test.
