package com.ctrip.framework.idgen.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralTest.class);

    public static void main(String[] args) {
        LOGGER.info("test1 {} test1", 100);
        LOGGER.info("test2 '{}' test2", "cc");
        System.exit(0);
    }

}
