package com.jack.graphql.interfaces.dto.filter;

import com.jack.graphql.utils.LocalDateUtils;
import com.jack.graphql.utils.StringConvertUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public enum DataType {

    SHORT(Short.class), INT(Integer.class), LONG(Long.class), STRING(String.class), DATE(LocalDate.class), DATE_TIME(LocalDateTime.class), BOOLEAN(Boolean.class), BIG_DECIMAL(BigDecimal.class), BIG_INTEGER(BigInteger.class);

    private Class clazz;

    DataType(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public static Comparable fromString(DataType dataType, String source) {
        Comparable result;
        switch (dataType) {
            case SHORT:
                result = StringConvertUtils.toShort(source);
                break;
            case INT:
                result = StringConvertUtils.toInt(source);
                break;
            case LONG:
                result = StringConvertUtils.toLong(source);
                break;
            case STRING:
                result = source;
                break;
            case DATE:
                result = LocalDateUtils.parseLocalDate(source, "yyyy-MM-dd");
                break;
            case DATE_TIME:
                result = LocalDateUtils.parseLocalDateTime(source, "yyyy-MM-dd hh24:MI:ss");
                break;
            case BOOLEAN:
                result = StringConvertUtils.toBool(source);
                break;
            case BIG_DECIMAL:
                result = StringConvertUtils.toBigDecimal(source);
                break;
            case BIG_INTEGER:
                result = StringConvertUtils.toBigInteger(source);
                break;
            default:
                result = null;
                break;
        }

        return result;
    }

    public static Comparable[] fromString(DataType dataType, String[] source) {
        if (null == source || dataType == null) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        Comparable[] result = new Comparable[source.length];
        for (int i = 0; i < source.length; i++) {
            result[i] = fromString(dataType, source[i]);
        }
        return result;
    }
}
