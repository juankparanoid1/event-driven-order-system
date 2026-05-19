CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               aggregate_type VARCHAR(50) NOT NULL,
                               aggregate_id UUID NOT NULL,
                               event_type VARCHAR(50) NOT NULL,
                               payload JSONB NOT NULL,
                               processed BOOLEAN DEFAULT FALSE,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_outbox_processed ON outbox_events(processed);
CREATE INDEX idx_outbox_aggregate ON outbox_events(aggregate_id);