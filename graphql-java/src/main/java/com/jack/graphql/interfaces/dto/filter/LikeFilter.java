package com.jack.graphql.interfaces.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jack.graphql.utils.StringUtil;

public class LikeFilter extends Filter {

    private boolean not;
    private String field;
    private String expression;

    public LikeFilter() {
        super(FilterType.Like);
    }

    public LikeFilter(String field, String expression) {
        this(false, field, expression);
    }

    public LikeFilter(boolean not, String field, String expression) {
        super(FilterType.Like);
        this.not = not;
        this.field = field;
        this.expression = expression;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return StringUtil.isNotBlank(field) && StringUtil.isNotBlank(expression);
    }

    @Override
    public String toString() {
        return String.format(" %s %s like %s ", field, not ? "not" : "", expression);
    }
}
