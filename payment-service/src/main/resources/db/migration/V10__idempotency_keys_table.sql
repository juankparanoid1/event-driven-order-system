CREATE TABLE idempotency_keys (
                                  id UUID PRIMARY KEY,
                                  idempotency_key VARCHAR(255) UNIQUE NOT NULL,
                                  request_hash VARCHAR(255) NOT NULL,
                                  response TEXT NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);