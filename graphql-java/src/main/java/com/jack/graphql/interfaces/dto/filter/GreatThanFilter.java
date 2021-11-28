package com.jack.graphql.interfaces.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jack.graphql.utils.StringUtil;

public class GreatThanFilter extends Filter{

    private String field;
    private boolean equal;
    private String value;
    private DataType dataType;

    public GreatThanFilter() {
        super(FilterType.GreatThan);
    }

    public GreatThanFilter(DataType dataType, String field, boolean equalFlag, String value) {
        super(FilterType.GreatThan);
        this.dataType = dataType;
        this.field = field;
        this.equal = equalFlag;
        this.value = value;
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

    public void setEqual(boolean equal) {
        this.equal = equal;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public boolean isEqual() {
        return equal;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return StringUtil.isNotBlank(field) && StringUtil.isNotEmpty(value);
    }


    @Override
    public String toString() {
        return  String.format("%s %s %s", field, equal ? " >= ": " > ", value);
    }
}
