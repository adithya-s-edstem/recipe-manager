# recipe-manager

A multi-user recipe manager built with **React**, **Spring Boot**, **PostgreSQL** and **Docker**. Users can store recipes with structured ingredients, steps, tags and photos. They can search with rich filters (text, tags, cuisine, difficulty, include/exclude ingredients, time, calories) and share recipes publicly, where others can favorite, rate, comment on and fork them.

## Documentation

- [Product Requirements (PRD)](docs/PRD.md): goals, personas, features, acceptance criteria, permissions
- [Technical Specification](docs/TECH_SPEC.md): architecture, data model, REST API, security, Docker, testing, CI

## Working with Claude Code

Project skills live in `.claude/skills/`, and project conventions are in [CLAUDE.md](CLAUDE.md).

| Skill | Use |
|---|---|
| `/create-ticket REC-1` or `/create-ticket milestone 3` | Turn PRD requirements into GitHub Issues |
| `/implement-ticket 12` | Branch, plan, code, test, and commit a ticket |
| `/verify` | Run backend, frontend, and Docker checks |
| `/raise-pr` | Push and open a PR from the template |
| `/review-ticket 34` | Review a PR against the ticket's acceptance criteria |
| `/add-migration add ratings table` | Add a Flyway migration that matches the Tech Spec |
| `/sync-docs "admins can edit recipes"` | Keep the PRD and Tech Spec in sync with decisions |

Typical loop: `create-ticket` → `implement-ticket` → `raise-pr` → `review-ticket`.

## Status

Planning. Implementation has not started yet.
