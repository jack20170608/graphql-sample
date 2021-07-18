create table t_order(
    id INTEGER identity,
    sequence_no VARCHAR(50) NOT NULL,
    customer_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL ,
    order_dt DATETIME,
    status VARCHAR(32),
    create_dt DATETIME,
    last_update_dt DATETIME,
    raw_string TEXT
)

go

create index idx_t_order_sequence_no on t_order (sequence_no)

go