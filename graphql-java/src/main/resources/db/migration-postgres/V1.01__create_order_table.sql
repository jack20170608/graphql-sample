create table t_order(
    id INTEGER primary key,
    sequence_no CHAR(50) NOT NULL,
    customer_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL ,
    order_dt TIMESTAMP ,
    status CHAR(32),
    create_dt TIMESTAMP ,
    last_update_dt TIMESTAMP ,
    raw_string TEXT
);

create sequence seq_t_order_id increment by 1 minvalue 1 no maxvalue start with 1;

create index idx_t_order_sequence_no on t_order (sequence_no);