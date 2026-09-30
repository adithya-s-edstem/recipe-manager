## Summary

<!-- What this PR does and why, in 1–3 sentences. -->

Closes #

**Requirements:** <!-- PRD IDs, e.g. REC-1, REC-2 -->

## Changes

<!-- Bullet list of the notable changes, grouped by area. -->

- **Backend:**
- **Frontend:**
- **DB migrations:** <!-- file names, or "none" -->
- **Docs:** <!-- PRD / TECH_SPEC updates, or "none" -->

## Acceptance criteria

<!-- Copy the checklist from the ticket and tick what this PR satisfies. Explain anything left unticked. -->

- [ ]

## How to test

<!-- Steps a reviewer can follow locally. -->

1. `docker compose up --build`
2.

## Test evidence

<!-- Paste the result lines from the tests, and screenshots for UI changes (mobile + desktop). -->

| Check | Result |
|---|---|
| Backend `./mvnw verify` | |
| Frontend lint / typecheck / tests | |
| Frontend build | |
| `docker compose build` | |

## Checklist

- [ ] One ticket per PR; branch named `<type>/<issue#>-<slug>`
- [ ] New or changed endpoints have integration tests and appear in Swagger
- [ ] Authorization enforced server-side (private recipes → 404 for others)
- [ ] Input validated on server (and client where there is a form)
- [ ] Flyway migration added for schema changes; no merged migration edited
- [ ] UI checked at 360 px and desktop widths; inputs labelled
- [ ] No secrets, debug logging, or commented-out code
- [ ] PRD / TECH_SPEC updated if behaviour or API changed

## Notes for reviewers

<!-- Trade-offs, follow-ups, known limitations. -->
