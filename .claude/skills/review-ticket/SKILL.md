---
name: review-ticket
description: Review the pull request (or local branch) for a ticket. Checks it against the issue's acceptance criteria, the PRD and Tech Spec, and the project conventions, then reports verified findings. Use when the user says "review #12", "review PR 34", "review this ticket", or "is ticket 12 done".
argument-hint: <issue-or-pr-number> [--comment]
---

# Review a ticket

Target: `$ARGUMENTS`

## 1. Resolve the target

- If a **PR number** is given, run `gh pr view <n> --json number,title,body,headRefName,baseRefName,files,closingIssuesReferences`. The ticket is the issue it closes.
- If an **issue number** is given, find its PR with `gh pr list --search "<n> in:body" --state open`. If there is none, review the local branch `*/<n>-*` against `main`.
- If nothing is given, review the current branch against `main`.

Collect the full diff with `gh pr diff <n>` or `git diff main...HEAD`, plus the ticket (`gh issue view <issue> --comments`).

## 2. Load the standard

Read the ticket's acceptance criteria and requirement IDs, the matching `docs/PRD.md` sections, the relevant `docs/TECH_SPEC.md` sections, and `CLAUDE.md`.

## 3. Review, in this order

1. **Acceptance criteria:** for each criterion, is it implemented, and is there a test that proves it? Build a table of criterion → code location → test → ✅/⚠️/❌.
2. **Correctness:** logic bugs, edge cases (nulls, empty lists, boundary values from the PRD validation rules), transactions, N+1 queries, and pagination/sort correctness.
3. **Security and permissions:** check against the PRD §4 matrix.
   - Is authorization done server-side?
   - Do private recipes return 404 to non-owners?
   - Is every input validated?
   - Are upload type and size checked by content?
   - Is there any `dangerouslySetInnerHTML`?
4. **Contract:** do the endpoints, status codes, DTO shapes and query parameters match TECH_SPEC §4? Is the OpenAPI annotation present? Do the frontend types match?
5. **Data:**
   - Are schema changes in a new Flyway migration?
   - Has any merged migration been edited?
   - Are constraints and indexes from TECH_SPEC §3.1 respected?
6. **Tests:**
   - Are there integration tests for new endpoints (happy path, 400, 401/403/404)?
   - Are there component tests for UI behaviour?
   - Do the tests actually assert the behaviour, or do they just run the code?
7. **Conventions and quality:**
   - Layering (Controller → Service → Repository)
   - No entities in controllers
   - Naming
   - Dead code and duplicated logic that could reuse existing code
   - Scope creep beyond the ticket
8. **Frontend UX:** mobile-first layout, labelled inputs, loading, empty and error states, and filter state kept in the URL.
9. **Docs:** if behaviour or the API changed, were the PRD and Tech Spec updated?

Check out the branch and run the `verify` skill, unless the user says not to. Include the results in the report.

## 4. Verify findings

Before reporting a finding, confirm it by reading the surrounding code or reproducing it. Drop anything you can't back up. Each finding needs a file:line, a concrete failure scenario, and a suggested fix.

## 5. Report

- **Verdict:** Approve / Request changes / Comment.
- The acceptance-criteria table.
- Findings ranked by severity: 🔴 blocker, 🟠 should fix, 🟡 nit.
- Verification results.

If `--comment` was passed, **ask the user to confirm first**. Then post the summary with `gh pr review <n> --comment --body-file <file>`, and post inline comments with `gh api repos/{owner}/{repo}/pulls/<n>/comments`. Never use `--approve` or `--request-changes` unless the user explicitly asks for it.
