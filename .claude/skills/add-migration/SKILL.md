---
name: add-migration
description: Add a Flyway database migration for a schema change (new table, column, index, constraint, trigger), following the Tech Spec data model. Use whenever an entity or the schema changes, or when the user says "add a migration", "change the schema", or "add a column".
argument-hint: <short description of the change>
---

# Add a Flyway migration

Change: `$ARGUMENTS`

## Rules

- Migrations live in `backend/src/main/resources/db/migration/`.
- Name them `V<next>__<snake_case_description>.sql`. Find `<next>` by taking the highest existing `V` number and adding 1. Check `origin/main` as well, so you don't collide with work that was merged in the meantime.
- **Never edit a migration that is already on `main`.** Write a new one instead. A migration may only be edited if it exists solely on the current, unmerged branch.
- `spring.jpa.hibernate.ddl-auto` must stay `validate`. The migration defines the schema, and the entities must match it.

## Follow the Tech Spec

Match `docs/TECH_SPEC.md` §3 exactly: table and column names (snake_case, plural tables), types (`bigint` identity PKs, `timestamptz`, `varchar` enums, `numeric` for grams and quantities), and FKs with the `ON DELETE` behaviour from §3.1. Add the constraints and indexes listed there, such as unique keys, `CHECK`s, the partial unique cover index, GIN/trigram indexes, and the generated `total_minutes` column.

If the change needs a schema the Tech Spec doesn't describe, point that out and offer `sync-docs`.

## Write it safely

- One logical change per migration. Include comments explaining anything non-obvious (triggers, generated columns).
- When adding a `NOT NULL` column to a table that has data, give it a default or backfill it within the same migration.
- Create extensions idempotently: `CREATE EXTENSION IF NOT EXISTS pg_trgm;`.
- Update the matching JPA entity (column names, `@Enumerated(EnumType.STRING)`, relationships) in the same commit.

## Verify

Run the backend tests (`verify backend`). The Testcontainers suite applies every migration to an empty Postgres, and Hibernate `validate` catches mismatches between entities and schema. Report the result.
