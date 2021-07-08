package com.jack.graphql.domain;

import com.google.common.collect.Maps;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.jack.graphql.utils.CollectionUtils;
import com.jack.graphql.utils.Constants;
import com.jack.graphql.utils.LocalDateUtils;
import com.jack.graphql.utils.StringConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

import static com.jack.graphql.utils.StringConvertUtils.toEnum;

public class Order implements Serializable, Comparable<Order>, Portable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Order.class);

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";

    private Long id;
    private String sequenceNo;
    private Long customerId;
    private Long productId;
    private LocalDateTime orderDatetime;
    private Status status;


    private LocalDateTime createDt;
    private LocalDateTime lastUpdateDt;

    private String[] rawString;

    public Order() {
    }

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

    @Override
    public int getFactoryId() {
        return Constants.HAZELCAST_FACTORY_ID;
    }

    @Override
    public int getClassId() {
        return Constants.HAZELCAST_ORDER_OBJECT_ID;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeLong(OrderField.id.name(), id);
        writer.writeUTF(OrderField.sequenceNo.name(), sequenceNo);
        writer.writeLong(OrderField.customerId.name(), customerId);
        writer.writeLong(OrderField.productId.name(),  productId);
        writer.writeUTF(OrderField.orderDatetime.name(), LocalDateUtils.format(orderDatetime, DATETIME_PATTERN));
        writer.writeUTF(OrderField.status.name(), StringConvertUtils.toStr(status));
        writer.writeUTF("createDt", LocalDateUtils.format(createDt, DATETIME_PATTERN));
        writer.writeUTF("lastUpdateDt", LocalDateUtils.format(lastUpdateDt, DATETIME_PATTERN));
        writer.writeUTFArray("rawString", rawString);
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        id = reader.readLong(OrderField.id.name());
        sequenceNo = reader.readUTF(OrderField.sequenceNo.name());
        customerId = reader.readLong(OrderField.customerId.name());
        productId = reader.readLong(OrderField.productId.name());
        orderDatetime = LocalDateUtils.parseLocalDateTime(reader.readUTF(OrderField.orderDatetime.name()) , DATETIME_PATTERN);
        status= toEnum(Status.class, reader.readUTF(OrderField.status.name()));

        createDt = LocalDateUtils.parseLocalDateTime(reader.readUTF("createDt") , DATETIME_PATTERN);
        lastUpdateDt = LocalDateUtils.parseLocalDateTime(reader.readUTF("lastUpdateDt") , DATETIME_PATTERN);
        rawString = reader.readUTFArray("rawString");

    }
}
