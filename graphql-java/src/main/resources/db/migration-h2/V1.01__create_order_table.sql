create table t_order(
    id INTEGER primary key identity ,
    sequence_no VARCHAR(50) NOT NULL,
    customer_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL ,
    order_dt TIMESTAMP ,
    status VARCHAR(32),
    create_dt TIMESTAMP ,
    last_update_dt TIMESTAMP ,
    raw_string TEXT
);

create index IDX_T_ORDER_SEQUENCE_NO on T_ORDER (SEQUENCE_NO);