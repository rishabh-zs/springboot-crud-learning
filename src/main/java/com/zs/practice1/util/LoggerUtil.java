package com.zs.practice1.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The type Logger util.
 */
public class LoggerUtil {
    /**
     * Gets logger.
     *
     * @param clazz the clazz
     * @return the logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}