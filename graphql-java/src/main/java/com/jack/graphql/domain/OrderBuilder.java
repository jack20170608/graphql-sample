package com.jack.graphql.domain;

import java.time.LocalDateTime;

public final class OrderBuilder {

    private Long id;
    private String sequenceNo;
    private Long customerId;
    private Long productId;
    private LocalDateTime orderDatetime;
    private Status status;
    private LocalDateTime createDt;
    private LocalDateTime lastUpdateDt;
    private String[] rawString;

    private OrderBuilder() {
    }

    public static OrderBuilder anOrder() {
        return new OrderBuilder();
    }

    public static OrderBuilder anOrder(Order order) {
        return new OrderBuilder()
            .withId(order.getId())
            .withSequenceNo(order.getSequenceNo())
            .withCustomerId(order.getCustomerId())
            .withProductId(order.getProductId())
            .withOrderDatetime(order.getOrderDatetime())
            .withOrderStatus(order.getStatus())
            .withCreateDt(order.getCreateDt())
            .withLastUpdateDt(order.getLastUpdateDt())
            .withRawString(order.getRaw());
    }

    public OrderBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OrderBuilder withSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
        return this;
    }

    public OrderBuilder withCustomerId(Long customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderBuilder withProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public OrderBuilder withOrderDatetime(LocalDateTime orderDatetime) {
        this.orderDatetime = orderDatetime;
        return this;
    }

    public OrderBuilder withOrderStatus(Status status) {
        this.status = status;
        return this;
    }

    public OrderBuilder withCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
        return this;
    }

    public OrderBuilder withLastUpdateDt(LocalDateTime lastUpdateDt) {
        this.lastUpdateDt = lastUpdateDt;
        return this;
    }

    public OrderBuilder withRawString(String[] rawString) {
        this.rawString = rawString;
        return this;
    }

    public Order build() {
        return new Order(id, sequenceNo, customerId, productId, orderDatetime, status, createDt, lastUpdateDt, rawString);
    }
}
