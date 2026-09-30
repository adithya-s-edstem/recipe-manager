-- Baseline: extensions the schema relies on.
-- pg_trgm backs the trigram GIN indexes used for ingredient and tag autocomplete (TECH_SPEC §3.1).
CREATE EXTENSION IF NOT EXISTS pg_trgm;
