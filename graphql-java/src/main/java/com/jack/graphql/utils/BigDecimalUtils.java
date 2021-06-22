package com.jack.graphql.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

public class BigDecimalUtils {

    public static final int MONEY_POINT = 2;


    public static Double format(double v, int point) {
        BigDecimal b = new BigDecimal(v);
        return b.setScale(point, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static Double formatRoundUp(double v, int point) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setMinimumFractionDigits(point);
        nf.setMaximumFractionDigits(point);
        return Double.valueOf(nf.format(v));
    }


    public static String moneyFormat(Double v) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        formater.setGroupingSize(3);
        formater.setRoundingMode(RoundingMode.FLOOR);
        return formater.format(v.doubleValue());
    }


    public static String doubleTrans(Double d) {
        if (Math.round(d) - d == 0) {
            return String.valueOf((long) d.doubleValue());
        }
        return String.valueOf(d);
    }


    public static Double add(double v1, double v2) {
        BigDecimal n1 = new BigDecimal(Double.toString(v1));
        BigDecimal n2 = new BigDecimal(Double.toString(v2));
        return n1.add(n2).doubleValue();
    }


    public static Double subtract(double v1, double v2) {
        BigDecimal n1 = new BigDecimal(Double.toString(v1));
        BigDecimal n2 = new BigDecimal(Double.toString(v2));
        return n1.subtract(n2).doubleValue();
    }

    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2) {
        if (Objects.isNull(v1) || Objects.isNull(v2)){
            return BigDecimal.ZERO;
        }
        return v1.multiply(v2);
    }

    public static Double multiply(double v1, double v2) {
        BigDecimal n1 = new BigDecimal(Double.toString(v1));
        BigDecimal n2 = new BigDecimal(Double.toString(v2));
        return n1.multiply(n2).doubleValue();
    }


    public static Double divide(double v1, double v2) {
        BigDecimal n1 = new BigDecimal(Double.toString(v1));
        BigDecimal n2 = new BigDecimal(Double.toString(v2));
        return n1.divide(n2, 10, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static int compare(double v1, double v2) {
        BigDecimal n1 = new BigDecimal(Double.toString(v1));
        BigDecimal n2 = new BigDecimal(Double.toString(v2));
        return n1.compareTo(n2);
    }

}
