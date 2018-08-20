package com.ctrip.platform.idgen.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SnowflakeConfig {

    protected static long workerId;
    protected static int timestampBits;
    protected static int workerIdBits;
    protected static int sequenceBits;
    protected static int sequenceInitRange;
    protected static int timestampShift;
    protected static int workerIdShift;
    protected static long timestampMask;
    protected static long maxWorkerId;
    protected static long sequenceMask;
    protected static long timestampReference;

    public static void initialize(final Map<String, String> properties) {
        workerId = parseLong(properties, getWorkerIdPropertyKey());
        timestampBits = parseInt(properties, Constants.TIMESTAMPBITS_PROPERTY_KEY);
        workerIdBits = parseInt(properties, Constants.WORKERIDBITS_PROPERTY_KEY);
        sequenceBits = parseInt(properties, Constants.SEQUENCEBITS_PROPERTY_KEY);
        sequenceInitRange = parseInt(properties, Constants.SEQUENCEINITRANGE_PROPERTY_KEY);
        timestampReference = parseDate(properties, Constants.TIMESTAMPREFERENCE_PROPERTY_KEY).getTime();

        timestampShift = workerIdBits + sequenceBits;
        workerIdShift = sequenceBits;
        timestampMask = ~(-1L << timestampBits);
        maxWorkerId = ~(-1L << workerIdBits);
        sequenceMask = ~(-1L << sequenceBits);

        validate();
    }

    private static void validate() {
        if (timestampBits + workerIdBits + sequenceBits > Constants.ID_MAX_BITS) {
            throw new RuntimeException("id bits overflow");
        }
        if (timestampReference > System.currentTimeMillis()) {
            throw new RuntimeException("time fallback");
        }
        if (System.currentTimeMillis() - timestampReference > timestampMask) {
            throw new RuntimeException("timestamp overflow");
        }
        if (workerId > maxWorkerId) {
            throw new RuntimeException("workerId overflow");
        }
        if (sequenceInitRange - 1 > sequenceMask) {
            throw new RuntimeException("sequenceInitRange overflow");
        }
    }

    private static String getWorkerIdPropertyKey() {
        return String.format(Constants.WORKERID_PROPERTY_KEY_PATTERN, getWorkerIdPropertyKeySuffix());
    }

    private static String getWorkerIdPropertyKeySuffix() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static int parseInt(final Map<String, String> properties, String key) {
        if (null == properties) {
            throw new RuntimeException("properties is empty");
        }
        if (!properties.containsKey(key)) {
            throw new RuntimeException("property of " + key + " not found");
        }
        int value = -1;
        try {
            value = Integer.parseInt(properties.get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException("property of " + key + " format exception");
        }
        if (value < 0) {
            throw new RuntimeException("property of " + key + " format exception");
        }
        return value;
    }

    private static long parseLong(final Map<String, String> properties, String key) {
        if (null == properties) {
            throw new RuntimeException("properties is empty");
        }
        if (!properties.containsKey(key)) {
            throw new RuntimeException("property of " + key + " not found");
        }
        long value = -1L;
        try {
            value = Long.parseLong(properties.get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException("property of " + key + " format exception");
        }
        if (value < 0) {
            throw new RuntimeException("property of " + key + " format exception");
        }
        return value;
    }

    private static Date parseDate(final Map<String, String> properties, String key) {
        if (null == properties) {
            throw new RuntimeException("properties is empty");
        }
        if (!properties.containsKey(key)) {
            throw new RuntimeException("property of " + key + " not found");
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIMESTAMPREFERENCE_DATE_FORMAT);
            return sdf.parse(properties.get(key));
        } catch (ParseException e) {
            throw new RuntimeException("property of " + key + " format exception");
        }
    }

}
