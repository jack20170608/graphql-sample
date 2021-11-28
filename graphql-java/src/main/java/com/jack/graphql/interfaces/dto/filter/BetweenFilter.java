package com.jack.graphql.interfaces.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jack.graphql.utils.StringUtil;

import java.util.Objects;

public class BetweenFilter extends Filter{

    private String field;
    private String left;
    private String right;
    private DataType dataType;

    public BetweenFilter() {
        super(FilterType.Between);
    }

    public BetweenFilter(DataType dataType, String field, String left, String right) {
        super(FilterType.Between);
        this.dataType = dataType;
        this.field = field;
        this.left = left;
        this.right = right;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getField() {
        return field;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return filterType == FilterType.Between && StringUtil.isNotBlank(field) && Objects.nonNull(left) && Objects.nonNull(right) ;
    }

    @Override
    public String toString() {
        return  "(" + field + " between " + left + " and " + right + ")";
    }
}
