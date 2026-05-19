ALTER TABLE idempotency_keys
ALTER COLUMN response TYPE JSONB
USING response::jsonb;