package com.jack.graphql.interfaces.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class LogicFilter extends Filter{

    public LogicFilter() {
        super(FilterType.Logic);
    }

    public LogicFilter(BooleanLogic logic, List<Filter> sub) {
        super(FilterType.Logic);
        this.logic = logic;
        this.sub = sub;
    }

    public BooleanLogic getLogic() {
        return logic;
    }

    public List<Filter> getSub() {
        return sub;
    }

    public void setLogic(BooleanLogic logic) {
        this.logic = logic;
    }

    public void setSub(List<Filter> sub) {
        this.sub = sub;
    }

    public enum BooleanLogic {
        AND, OR
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        if (null == sub){
            return false;
        }
        for (Filter filter : sub){
            if (filter == null || !filter.isValid()){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        boolean first = true;
        for (Filter filter : sub) {
            if (first) {
                stringBuilder.append(filter.toString());
                first = false;
            }else {
                stringBuilder.append(String.format(" %s %s ", this.logic.name().toLowerCase() , filter.toString()));
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private BooleanLogic logic;
    private List<Filter> sub;

}
