CREATE TABLE payments (
                          id UUID PRIMARY KEY,
                          order_id UUID NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          provider VARCHAR(50),
                          transaction_reference VARCHAR(100),
                          amount NUMERIC(10,2) NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                          CONSTRAINT fk_payments_order
                              FOREIGN KEY (order_id)
                                  REFERENCES orders(id)
                                  ON DELETE CASCADE
);

CREATE INDEX idx_payments_order_id ON payments(order_id);