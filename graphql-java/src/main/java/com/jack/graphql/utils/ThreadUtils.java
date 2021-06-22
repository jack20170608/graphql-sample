package com.jack.graphql.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(ThreadUtils.class);

    public static void quietSleep(int second){
        if (second < 0){
            second = 100;
        }
        try {
            Thread.sleep( second);
        } catch (InterruptedException e) {
            LOGGER.error("Error ", e);
        }
    }
}
