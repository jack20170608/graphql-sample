package com.jack.graphql.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OrderField {


    id("ID", 0)
    , sequenceNo("SEQUENCE_NO", 1)
    , customerId("CUSTOMER_ID", 2)
    , productId("PRODUCT_ID", 3)
    , price("PRICE", 4)
    , quantity("QUANTITY", 5)
    , totalAmount("TOTAL_AMOUNT", 6)
    , orderDatetime("ORDER_DATETIME", 7)
    , payDatetime("PAY_DATETIME", 8)
    , deliveryDatetime("DELIVERY_DATETIME", 9)
    , status("STATUS", 10)
    , comments("COMMENTS", 11);

    private String name;
    private int index;

    OrderField(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static List<OrderField> ALL_FIELDS = Arrays.stream(OrderField.values()).collect(Collectors.toList());
}