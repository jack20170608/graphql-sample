package com.jack.graphql.interfaces.dto;

import com.google.common.collect.Lists;
import com.jack.graphql.interfaces.dto.filter.*;

import java.io.Serializable;
import java.util.List;

public class CommonQueryDto implements Serializable {

    //query fields
    private List<String> selectFields;

    //Filter
    private Filter filter;

    //Order by
    private List<OrderBy> orderByList;

    public void setSelectFields(List<String> selectFields) {
        this.selectFields = selectFields;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setOrderByList(List<OrderBy> orderByList) {
        this.orderByList = orderByList;
    }

    public List<String> getSelectFields() {
        return selectFields;
    }

    public Filter getFilter() {
        return filter;
    }

    public List<OrderBy> getOrderByList() {
        return orderByList;
    }

    public enum Order {
        DESC, ASC
    }

    public static class OrderBy {
        String field;
        Order order;

        public OrderBy() {
        }

        public OrderBy(String field, Order order) {
            this.field = field;
            this.order = order;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        @Override
        public String toString() {
            return "OrderBy{" +
                "field='" + field + '\'' +
                ", order=" + order +
                '}';
        }
    }

    @Override
    public String toString() {
        return "CommonQueryDto{" +
            "selectFields=" + selectFields +
            ", filter=" + filter +
            ", orderByList=" + orderByList +
            '}';
    }

    public static CommonQueryDto dummyQueryObj() {
        CommonQueryDto queryDto = new CommonQueryDto();

        queryDto.filter = dummyFilter();

        queryDto.selectFields = Lists.newArrayList("aaa", "bbb");

        queryDto.orderByList = Lists.newArrayList(new OrderBy("aaa", Order.ASC));


        return queryDto;

    }


    public static Filter simpleFilter() {
        return new BetweenFilter(DataType.INT, "age", "10", "20");
    }

    public static Filter dummyFilter() {
        BetweenFilter ageFilter = new BetweenFilter(DataType.INT, "age", "10", "20");
        EqualFilter sexFilter = new EqualFilter(DataType.STRING, false, "sex", "boy");

        GreatThanFilter greatThanFilter = new GreatThanFilter(DataType.LONG, "id", true, "100");
        LessThanFilter lessThanFilter = new LessThanFilter(DataType.LONG, "id", false, "200");

        LikeFilter nameLike = new LikeFilter("name", "%007");

        InFilter b1Filter = new InFilter(DataType.DATE, false, "birthday", new String[]{"2021-01-01", "2021-02-01"});
        InFilter b2Filter = new InFilter(DataType.DATE, false, "birthday", new String[]{"2020-01-01", "2021-02-01"});
        LogicFilter or = new LogicFilter(LogicFilter.BooleanLogic.OR, Lists.newArrayList(b1Filter, b2Filter));

        LogicFilter and = new LogicFilter(LogicFilter.BooleanLogic.AND
            , Lists.newArrayList(ageFilter, sexFilter, or, nameLike, greatThanFilter, lessThanFilter));

        return and;
    }

}
