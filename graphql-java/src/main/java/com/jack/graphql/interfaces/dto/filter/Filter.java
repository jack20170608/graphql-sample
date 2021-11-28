package com.jack.graphql.interfaces.dto.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "filterType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BetweenFilter.class, name = "Between"),
    @JsonSubTypes.Type(value = EqualFilter.class, name = "Equal"),
    @JsonSubTypes.Type(value = GreatThanFilter.class, name = "GreatThan"),
    @JsonSubTypes.Type(value = InFilter.class, name = "In"),
    @JsonSubTypes.Type(value = LessThanFilter.class, name = "LessThan"),
    @JsonSubTypes.Type(value = LikeFilter.class, name = "Like"),
    @JsonSubTypes.Type(value = LogicFilter.class, name = "Logic")
})
public class Filter {

    protected FilterType filterType;

    public Filter(FilterType filterType) {
        this.filterType = filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public boolean isValid() {
        return true;
    }

}
