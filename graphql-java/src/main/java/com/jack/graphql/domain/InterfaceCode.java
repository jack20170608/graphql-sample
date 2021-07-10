package com.jack.graphql.domain;

import com.jack.graphql.utils.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;

public class InterfaceCode {

    private Long id;
    private final String code;
    private Long counter;
    private final LocalDateTime createDt;
    private LocalDateTime lastUpdateDt;
    private List<OrderField> orderFieldList;

    public InterfaceCode(Long id, String code, Long counter, List<OrderField> orderFieldList, LocalDateTime createDt, LocalDateTime lastUpdateDt) {
        this.id = id;
        this.code = code;
        this.counter = counter;
        this.orderFieldList = orderFieldList;
        this.createDt = createDt;
        this.lastUpdateDt = lastUpdateDt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Long getCounter() {
        return counter;
    }

    public List<OrderField> getOrderFieldList() {
        return orderFieldList;
    }

    public void setOrderFieldList(List<OrderField> orderFieldList) {
        this.orderFieldList = orderFieldList;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

    public void setLastUpdateDt(LocalDateTime lastUpdateDt) {
        this.lastUpdateDt = lastUpdateDt;
    }

    @Override
    public String toString() {
        return "InterfaceCode{" +
            "id=" + id +
            ", code='" + code + '\'' +
            ", counter=" + counter +
            ", createDt=" + createDt +
            ", lastUpdateDt=" + lastUpdateDt +
            '}';
    }

    public static InterfaceCode of(String code, List<OrderField> orderFieldList) {
        return of(code, orderFieldList, 0L);
    }

    public static InterfaceCode of(String code, List<OrderField> orderFieldList, Long counter) {
        return new InterfaceCode(IdGenerator.getNextIdLong(), code, counter, orderFieldList, LocalDateTime.now(), LocalDateTime.now());
    }

}
