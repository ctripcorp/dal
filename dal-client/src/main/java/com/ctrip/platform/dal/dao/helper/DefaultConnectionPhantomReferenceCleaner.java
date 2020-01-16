package com.ctrip.platform.dal.dao.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lilj on 2018/4/12.
 */
public class DefaultConnectionPhantomReferenceCleaner implements ConnectionPhantomReferenceCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConnectionPhantomReferenceCleaner.class);
    private static AtomicReference<ScheduledExecutorService> defaultConnectionPhantomReferenceCleanerRef = new AtomicReference<>();
    private static final Integer DEFAULT_INTERVAL = 900;
    private static String driverClassName = "com.mysql.jdbc.NonRegisteringDriver";
    private static String fieldName = "connectionPhantomRefs";
    private static Class<?> nonRegisteringDriver;
    private static Field connectionPhantomReference;
    private static AtomicBoolean started = new AtomicBoolean(false);

    static {
        Runtime.getRuntime().addShutdownHook(new CustomThreadFactory("DefaultConnectionPhantomReferenceCleaner").newThread(new Runnable() {
            public void run() {
                shutdown();
            }
        }));
    }

    @Override
    public void start() throws Exception {
        if (started.getAndSet(true))
            return;
        try {
            nonRegisteringDriver = Class.forName(driverClassName);
            connectionPhantomReference = nonRegisteringDriver.getDeclaredField(fieldName);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(connectionPhantomReference, connectionPhantomReference.getModifiers() & ~Modifier.FINAL);
            connectionPhantomReference.setAccessible(true);
        } catch (ClassNotFoundException e) {
            LOGGER.error(String.format("Class %s not found", driverClassName), e);
            started.compareAndSet(true,false);
            return;
        }
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new CustomThreadFactory("DefaultConnectionPhantomReferenceCleaner"));
        executor.scheduleWithFixedDelay(new ConnectionPhantomReferenceCleanUpThread(), DEFAULT_INTERVAL, DEFAULT_INTERVAL, TimeUnit.SECONDS);
        defaultConnectionPhantomReferenceCleanerRef.set(executor);
    }


    private static class ConnectionPhantomReferenceCleanUpThread implements Runnable {
        @Override
        public void run() {
            try {
                ((ConcurrentHashMap) connectionPhantomReference.get(nonRegisteringDriver)).clear();
                LOGGER.info(String.format("ConnectionPhantomReference cleaned."));
            } catch (Exception ex) {
                LOGGER.warn(String.format("Cleaning ConnectionPhantomReference Error"), ex.getMessage());
            }
        }
    }


    private static void shutdown() {
        if (defaultConnectionPhantomReferenceCleanerRef.get() == null)
            return;
        defaultConnectionPhantomReferenceCleanerRef.get().shutdown();
        defaultConnectionPhantomReferenceCleanerRef.set(null);
        started.compareAndSet(true,false);
    }
}
