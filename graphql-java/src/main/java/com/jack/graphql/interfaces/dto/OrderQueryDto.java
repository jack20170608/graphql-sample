package com.jack.graphql.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jack.graphql.domain.OrderField;
import com.jack.graphql.domain.Status;
import com.jack.graphql.utils.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderQueryDto implements Serializable {

    private String sequenceNo;
    private Collection<Long> customerIds;
    private Collection<Long> productIds;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssSSS")
    private LocalDateTime orderDateTimeMin;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssSSS")
    private LocalDateTime orderDateTimeMax;
    private Set<Status> statusSet;

    private List<OrderField> orderFieldList;

    //used for paging
    private int pageSize = 100;
    private int pageNum = 1;
    //Used to cache the query result
    private String queryKey;
    //identify to the if need to cache the cache result
    private boolean cacheQueryResult;

    public OrderQueryDto() {
    }

    public OrderQueryDto(String sequenceNo, Collection<Long> customerIds, Collection<Long> productIds, LocalDateTime orderDateTimeMin, LocalDateTime orderDateTimeMax, Set<Status> statusSet, List<OrderField> orderFieldList, int pageSize, int pageNum
        , String queryKey, boolean cacheQueryResult) {
        this.sequenceNo = sequenceNo;
        this.customerIds = customerIds;
        this.productIds = productIds;
        this.orderDateTimeMin = orderDateTimeMin;
        this.orderDateTimeMax = orderDateTimeMax;
        this.statusSet = statusSet;
        this.orderFieldList = orderFieldList;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.queryKey = queryKey;
        this.cacheQueryResult = cacheQueryResult;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public Collection<Long> getCustomerIds() {
        return customerIds;
    }

    public Collection<Long> getProductIds() {
        return productIds;
    }

    public LocalDateTime getOrderDateTimeMin() {
        return orderDateTimeMin;
    }

    public LocalDateTime getOrderDateTimeMax() {
        return orderDateTimeMax;
    }

    public Set<Status> getStatusSet() {
        return statusSet;
    }

    //Add the default value
    public List<OrderField> getOrderFieldList() {
        return CollectionUtils.isEmpty(this.orderFieldList)
            ? Arrays.stream(OrderField.values()).collect(Collectors.toList())
            : this.orderFieldList;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public void setCustomerIds(Collection<Long> customerIds) {
        this.customerIds = customerIds;
    }

    public void setProductIds(Collection<Long> productIds) {
        this.productIds = productIds;
    }

    public void setOrderDateTimeMin(LocalDateTime orderDateTimeMin) {
        this.orderDateTimeMin = orderDateTimeMin;
    }

    public void setOrderDateTimeMax(LocalDateTime orderDateTimeMax) {
        this.orderDateTimeMax = orderDateTimeMax;
    }

    public void setStatusSet(Set<Status> statusSet) {
        this.statusSet = statusSet;
    }

    public void setOrderFieldList(List<OrderField> orderFieldList) {
        this.orderFieldList = orderFieldList;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize < 0 ? 100 : pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum <= 0 ? 1 : pageNum;
    }

    public String getQueryKey() {
        return queryKey;
    }

    public void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }

    public boolean isCacheQueryResult() {
        return cacheQueryResult;
    }

    public void setCacheQueryResult(boolean cacheQueryResult) {
        this.cacheQueryResult = cacheQueryResult;
    }

    @Override
    public String toString() {
        return "OrderQueryDto{" +
            "sequenceNo='" + sequenceNo + '\'' +
            ", customerIds=" + customerIds +
            ", productIds=" + productIds +
            ", orderDateTimeMin=" + orderDateTimeMin +
            ", orderDateTimeMax=" + orderDateTimeMax +
            ", statusSet=" + statusSet +
            ", orderFieldList=" + orderFieldList +
            ", pageSize=" + pageSize +
            ", pageNum=" + pageNum +
            ", queryKey='" + queryKey + '\'' +
            '}';
    }
}
