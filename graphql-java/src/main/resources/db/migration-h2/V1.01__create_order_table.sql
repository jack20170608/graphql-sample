create table T_ORDER(
    ID INTEGER primary key,
    SEQUENCE_NO CHAR(50) NOT NULL,
    CUSTOMER_ID INTEGER NOT NULL,
    PRODUCT_ID INTEGER NOT NULL ,
    ORDER_DT TIMESTAMP ,
    STATUS CHAR(32),
    CREATE_DT TIMESTAMP ,
    LAST_UPDATE_DT TIMESTAMP ,
    RAW_STRING TEXT
);

create sequence SEQ_T_ORDER_ID increment by 1 minvalue 1 no maxvalue start with 1;

create index IDX_T_ORDER_SEQUENCE_NO on T_ORDER (SEQUENCE_NO);