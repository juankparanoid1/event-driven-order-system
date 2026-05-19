ALTER TABLE outbox_events
    ADD updated_at TIMESTAMP NOT NULL DEFAULT NOW();
