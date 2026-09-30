---
name: implement-ticket
description: Implement a GitHub Issue ticket end to end. Reads the issue, creates the branch, plans against the PRD and Tech Spec, writes the code and tests, runs verification, and commits. Use when the user says "implement #12", "work on ticket 12", "pick up issue 12", or "build REC-1".
argument-hint: <issue-number>
---

# Implement a ticket

Ticket: `$ARGUMENTS`. If this is empty, ask for the issue number. If it is a PRD ID such as `REC-1` and not an issue number, find the issue with `gh issue list --search "REC-1 in:body"`. If no issue exists, offer to run the `create-ticket` skill first.

## 1. Understand

1. `gh issue view <n> --comments`. Note the requirement IDs, acceptance criteria, scope, out-of-scope items, and dependencies.
2. If a "Blocked by #x" issue is still open, stop and tell the user.
3. Read the sections of `docs/PRD.md` for every requirement ID, and the relevant sections of `docs/TECH_SPEC.md` (data model, API tables, conventions). Read `CLAUDE.md`.
4. Explore the existing code the ticket touches. Reuse existing entities, DTOs, services, components and test fixtures. Don't duplicate them.
5. If the ticket, PRD and Tech Spec contradict each other, or an acceptance criterion is ambiguous, ask the user before writing code.

## 2. Branch

```bash
git fetch origin && git switch main && git pull --ff-only
git switch -c <type>/<n>-<short-slug>     # e.g. feat/12-recipe-crud
```

If the working tree is dirty, stop and ask. Never stash or discard someone's changes.

## 3. Plan

Write a short plan in chat before coding. It maps **each acceptance criterion** to the code change and the test that proves it. For large tickets, list the files you will add or change.

## 4. Implement

Follow `CLAUDE.md` conventions. In particular:

- **Backend:**
  - Code flows Controller → Service → Repository, with record DTOs and Bean Validation.
  - Authorization goes in the service.
  - Return `ProblemDetail` errors with the status codes from TECH_SPEC §4.7.
- **Schema changes:** use the `add-migration` skill. Never use `ddl-auto=update`.
- **Frontend:**
  - Use the typed API client in `src/api`, TanStack Query hooks, and React Hook Form + Zod.
  - Keep filter state in the URL.
  - Build mobile-first.
- **Tests alongside code:** add a MockMvc + Testcontainers integration test for every endpoint (happy path, validation 400, auth 401/403/404) and a component test for UI behaviour. Name or tag tests with the requirement ID, e.g. `@DisplayName("REC-1: ...")`.
- Keep the diff within the ticket's scope. Note unrelated problems you find as follow-ups instead of fixing them.

## 5. Verify

Run the `verify` skill. Fix failures until everything passes. If something can't pass (for example, Docker isn't available), say so plainly with the output.

For UI tickets, also start the app and check the flow in the browser at 360 px and desktop widths. The chrome-devtools MCP tools can do this.

## 6. Commit

- Use Conventional Commits that reference the issue, e.g. `feat(recipe): add create/update endpoints (#12)`, with the requirement IDs in the body.
- Make small logical commits. Don't bundle unrelated changes.
- Stage specific paths. Don't use `git add -A` without first checking `git status`.

## 7. Hand off

Report:
- Each acceptance criterion with ✅ or ❌, and where it is tested.
- The verification results.
- Any follow-ups, and whether the PRD or Tech Spec needs updating (offer `sync-docs`).

Then offer to run `raise-pr`. **Don't push or open a PR unless the user asks.**
