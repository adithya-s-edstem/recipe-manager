---
name: verify
description: Run this project's full check suite (backend tests, frontend lint, typecheck, tests and build, docker compose build) and report the actual pass/fail results. Use before committing, before raising a PR, during review, or when the user says "run the tests", "verify", "check everything", or "does it build".
argument-hint: "[backend|frontend|docker|all]"
---

# Verify

Scope: `$ARGUMENTS` (default `all`). Run from the repo root. Skip any part whose directory or script doesn't exist yet, and report it as **skipped (not present)**, not as passed.

## Backend (`backend/`)

```bash
cd backend && ./mvnw -B verify
```

- Testcontainers needs Docker. If Docker isn't running, report that clearly. Don't disable the integration tests to get a green result.
- On failure, read `target/surefire-reports` / `target/failsafe-reports` and summarise the failing tests with their assertion messages.

## Frontend (`frontend/`)

```bash
cd frontend
[ -d node_modules ] || npm ci
npm run lint
npm run typecheck
npm test -- --run
npm run build
```

Run each command even if an earlier one fails, so the report is complete.

## Docker

```bash
docker compose config -q && docker compose build
```

Only when the user asks for a smoke test (or `review-ticket` needs one):
1. `docker compose up -d`
2. Wait until `curl -fs localhost:8080/actuator/health` reports UP.
3. Check that `curl -fs localhost:3000` returns the SPA.
4. `docker compose down` afterwards. **Never** use `down -v`, because that deletes the data volumes.

## Report

Show a table with each check and its result (✅ pass / ❌ fail / ⏭️ skipped), with the key output line (e.g. `Tests run: 84, Failures: 0`). For failures, give file:line and the error. Never claim a check passed if it wasn't run. Don't fix failures inside this skill unless the user asked. Return the results to whatever invoked it.
