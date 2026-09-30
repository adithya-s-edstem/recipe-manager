---
name: create-ticket
description: Create GitHub Issue tickets from PRD requirement IDs, a Tech Spec milestone, or a free-form idea, using .github/ISSUE_TEMPLATE/ticket.md. Use when the user says "create a ticket for REC-1", "break down milestone 3", "make issues for the auth work", or "file a ticket".
argument-hint: <REQ-IDs | "milestone N" | description>
---

# Create tickets

Input: `$ARGUMENTS`

## 1. Gather context

- Read `docs/PRD.md` and `docs/TECH_SPEC.md`, especially §13 Milestones, §3 Data model and §4 API.
- List existing issues with `gh issue list --state all --limit 200 --json number,title,body,state` to avoid duplicates and to find dependencies. If a requirement is already covered by an issue, say so and don't recreate it.

## 2. Slice the work

- **Requirement IDs:** usually one ticket, or split if it spans large backend and frontend chunks.
- **"milestone N":** break the milestone from TECH_SPEC §13 into tickets that are each roughly **one PR, reviewable in under 30 minutes**. Prefer vertical slices (endpoint + UI + tests) over layer-only tickets, except for foundation work such as a schema migration or the project skeleton.
- **Free-form idea:** map it to requirement IDs. If it isn't in the PRD, point that out and ask whether to add it (via `sync-docs`) or file it as out-of-scope.

## 3. Write each ticket

Follow `.github/ISSUE_TEMPLATE/ticket.md` exactly:

- **Title:** imperative, prefixed with the area, e.g. `Recipe: create and edit recipes (REC-1, REC-2)`.
- **Goal**, **Requirements** (IDs) and **Milestone**.
- **Scope:** backend, frontend and DB, plus an explicit **Out of scope** list.
- **Acceptance criteria:** copied or refined from the PRD. Each item must be testable and specific (values, status codes, messages). Include permission cases from PRD §4.
- **Technical notes:** the endpoints, entities, tables and TECH_SPEC section references.
- **Test plan** and **Dependencies** (`Blocked by #x`).

## 4. Confirm, then create

Show the user the proposed list (title, IDs, dependencies) and **wait for confirmation** before creating anything. Then, for each ticket:

```bash
gh issue create --title "<title>" --body-file <file> --label enhancement [--label "area:backend"] [--milestone "<name>"]
```

If a label or GitHub milestone doesn't exist yet, ask before creating it with `gh label create` or `gh api repos/{owner}/{repo}/milestones -f title=...`.

Create tickets in dependency order so that `Blocked by #x` can reference real numbers.

## 5. Report

List the created issue numbers and URLs in dependency order, and suggest which one to implement first.
