package com.jack.graphql.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jack.graphql.domain.Order;
import com.jack.graphql.domain.Status;

import java.time.LocalDateTime;

public class OrderVO {

    private Long id ;
    private String sequenceNo;
    private Long customerId;
    private Long productId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssSSS")
    private LocalDateTime orderDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssSSS")
    private LocalDateTime createDt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssSSS")
    private LocalDateTime lastUpdateDt;

    private Status status;
    private String content;


    public OrderVO(Long id, String sequenceNo, Status status, Long customerId, Long productId, LocalDateTime orderDateTime, LocalDateTime createDt
        , LocalDateTime lastUpdateDt, String content) {
        this.id = id;
        this.sequenceNo = sequenceNo;
        this.customerId = customerId;
        this.status = status;
        this.productId = productId;
        this.orderDateTime = orderDateTime;
        this.createDt = createDt;
        this.lastUpdateDt = lastUpdateDt;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public Status getStatus() {
        return status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public String getContent() {
        return content;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Order order){
        return new Builder()
            .withId(order.getId())
            .withSequenceNo(order.getSequenceNo())
            .withStatus(order.getStatus())
            .withCustomerId(order.getCustomerId())
            .withProductId(order.getProductId())
            .withOrderDateTime(order.getOrderDatetime())
            .withCreateDt(order.getCreateDt())
            .withLastUpdateDt(order.getLastUpdateDt())
            ;
    }


    public static final class Builder {
        private Long id ;
        private String sequenceNo;
        private Status status;
        private Long customerId;
        private Long productId;
        private LocalDateTime orderDateTime;
        private LocalDateTime createDt;
        private LocalDateTime lastUpdateDt;
        private String content;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withSequenceNo(String sequenceNo) {
            this.sequenceNo = sequenceNo;
            return this;
        }

        public Builder withStatus(Status status){
            this.status = status;
            return this;
        }

        public Builder withCustomerId(Long customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder withOrderDateTime(LocalDateTime orderDateTime) {
            this.orderDateTime = orderDateTime;
            return this;
        }

        public Builder withCreateDt(LocalDateTime createDt) {
            this.createDt = createDt;
            return this;
        }

        public Builder withLastUpdateDt(LocalDateTime lastUpdateDt) {
            this.lastUpdateDt = lastUpdateDt;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public OrderVO build() {
            return new OrderVO(id, sequenceNo, status, customerId, productId, orderDateTime, createDt, lastUpdateDt, content);
        }
    }
}
