CREATE TABLE inventory (
                           id UUID PRIMARY KEY,
                           product_id UUID NOT NULL,
                           available_quantity INT NOT NULL CHECK (available_quantity >= 0),
                           reserved_quantity INT NOT NULL DEFAULT 0 CHECK (reserved_quantity >= 0),
                           updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

                           CONSTRAINT unique_product_inventory UNIQUE (product_id)
);