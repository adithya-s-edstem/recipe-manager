---
name: sync-docs
description: Keep docs/PRD.md and docs/TECH_SPEC.md in sync with decisions and code, for example after a requirement changes, an open question is answered, a (default) assumption is confirmed, or the API or schema diverges from the spec. Use when the user says "update the docs", "we decided X", "answer open question 2", or when another skill detects a mismatch between code and docs.
argument-hint: <decision or change>
---

# Sync the docs

Change: `$ARGUMENTS`

## 1. Find everything affected

Search both docs for the topic (requirement IDs, field names, endpoint paths, and the *(default)* marker). A single decision often touches several places. For example, "admins can edit recipes" affects the PRD §4 matrix, the PRD §8 open questions, TECH_SPEC §4.2 auth columns, and the service authorization notes.

If code already exists, also check whether it matches the new decision. List any code that would need to change, and suggest a ticket (`create-ticket`) for it rather than changing code here.

## 2. Edit

- Keep requirement IDs stable. Never renumber. For a new requirement, use the next free number in its group. For a removed one, strike it through and give a reason instead of deleting it.
- When a *(default)* is confirmed, remove the marker. When it's changed, update the value and remove the marker.
- When an open question in PRD §8 is answered, move the answer into the relevant section and remove the question.
- Update the "Last updated" date in the header of each doc you change.
- Keep the PRD (the *what* and *why*) and the Tech Spec (the *how*) consistent with each other. Every endpoint change in TECH_SPEC §4 must still map to a PRD requirement.

## 3. Report

Show a short summary of each change, with file and section, plus any code or tickets that are now out of date. Commit with `docs: <summary>` only if the user asks.
