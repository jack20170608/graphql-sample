package com.jack.graphql.interfaces.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jack.graphql.utils.StringUtil;

public class EqualFilter extends Filter{

    private boolean not;

    private DataType dataType;

    private String field;

    private String value;

    public EqualFilter() {
        super(FilterType.Equal);
    }

    public EqualFilter(DataType dataType, String field, String value) {
        this(dataType, false, field, value);
    }

    public EqualFilter(DataType dataType, boolean notFlag, String field, String value) {
        super(FilterType.Equal);
        this.dataType = dataType;
        this.not = notFlag;
        this.field = field;
        this.value = value;
    }

    public boolean isNot() {
        return not;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return StringUtil.isNotBlank(field) && StringUtil.isNotEmpty(value);
    }

    @Override
    public String toString() {
        return String.format(" %s %s %s ", field, not ? " != ": " = ", value);
    }
}
