package com.jack.graphql.domain;

import com.google.common.collect.Maps;
import com.jack.graphql.utils.CollectionUtils;
import com.jack.graphql.utils.Constants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class Order implements Serializable, Comparable<Order> {

    private final Long id;
    private final String sequenceNo;
    private final Long customerId;
    private final Long productId;
    private final LocalDateTime orderDatetime;
    private final Status status;


    private final LocalDateTime createDt;
    private final LocalDateTime lastUpdateDt;

    private final String[] rawString;

    public Order(Long id, String sequenceNo, Long customerId, Long productId, LocalDateTime orderDatetime, Status status, LocalDateTime createDt, LocalDateTime lastUpdateDt, String[] rawString) {
        this.id = id;
        this.sequenceNo = sequenceNo;
        this.customerId = customerId;
        this.productId = productId;
        this.orderDatetime = orderDatetime;
        this.status = status;
        this.createDt = createDt;
        this.lastUpdateDt = lastUpdateDt;
        this.rawString = rawString;
    }

    public Long getId() {
        return id;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public LocalDateTime getOrderDatetime() {
        return orderDatetime;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
    }

    public String getRawString() {
        return String.join(Constants.SPLI_CHARACTER, rawString);
    }

    public String[] getRaw(){
        return rawString;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return sequenceNo.equals(order.sequenceNo) && customerId.equals(order.customerId) && productId.equals(order.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequenceNo, customerId, productId);
    }

    public String getByFields(List<OrderField> orderFieldList) {
        if (CollectionUtils.isEmpty(orderFieldList)) {
            return null;
        }
        StringJoiner stringJoiner = new StringJoiner(Constants.SPLI_CHARACTER);
        orderFieldList.forEach(field -> stringJoiner.add(rawString[field.getIndex()]));
        return stringJoiner.toString();
    }

    public Map<OrderField, String> getByFields(Set<OrderField> fieldSet) {
        Map<OrderField, String> resultMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(fieldSet)) {
            return resultMap;
        }
        fieldSet.forEach(f -> {
            resultMap.put(f, rawString[f.getIndex()]);
        });
        return resultMap;
    }


    @Override
    public int compareTo(Order o) {
        return this.getId().compareTo(o.getId());
    }
}
