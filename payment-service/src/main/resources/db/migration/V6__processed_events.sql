CREATE TABLE processed_events (
                                  id UUID PRIMARY KEY,
                                  event_id UUID NOT NULL,
                                  service_name VARCHAR(50) NOT NULL,
                                  processed_at TIMESTAMP NOT NULL DEFAULT NOW(),

                                  CONSTRAINT uq_event_service UNIQUE (event_id, service_name)
);