package com.jack.graphql.interfaces.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jack.graphql.utils.StringConvertUtils;
import com.jack.graphql.utils.StringUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InFilter extends Filter {

    //notFlag true means not
    private boolean not;
    private String field;
    private String[] values;
    private DataType dataType;

    public InFilter() {
        super(FilterType.In);
    }

    public InFilter(DataType dataType, String fieldName, String[] values) {
        this(dataType, false, fieldName, values);
    }

    public InFilter(DataType dataType, boolean not, String fieldName, String[] values) {
        super(FilterType.In);
        this.dataType = dataType;
        this.not = not;
        this.field = fieldName;
        this.values = values;
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

    public String[] getValues() {
        return values;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        List<String> strValues = Stream.of(this.values).map(StringConvertUtils::toStr).collect(Collectors.toList());
        return field +
            (not ? " not in [ " : " in [") +
            String.join(",", strValues) +
            "]";
    }

    @Override
    @JsonIgnore
    public boolean isValid() {
        return StringUtil.isNotBlank(field) && Objects.nonNull(values) && values.length > 0;
    }
}
