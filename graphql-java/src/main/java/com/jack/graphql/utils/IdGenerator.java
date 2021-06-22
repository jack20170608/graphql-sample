package com.jack.graphql.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {
    private IdGenerator() {
    }

    private static final AtomicInteger shortInteger = new AtomicInteger(0);

    private static final String DATATIME_PATTERN = "yyyyMMddHHmmssSSS";

    private static final String S0 = "0";


    public static String getNextId() {
        String time = LocalDateUtils.format(LocalDateTime.now(), DATATIME_PATTERN);
        int intValue = shortInteger.getAndIncrement();
        if (shortInteger.get() >= 1000000) {
            shortInteger.set(0);
        }
        return time.concat(StringUtils.leftPad(Integer.toString(intValue), 5, S0));
    }

    public static Long getNextIdLong() {
        String nextId = getNextId();
        return Long.parseLong(nextId);
    }
}
