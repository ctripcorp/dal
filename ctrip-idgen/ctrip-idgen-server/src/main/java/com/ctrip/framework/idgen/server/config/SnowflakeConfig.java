package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class SnowflakeConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeConfig.class);

    private static final String TIMESTAMPBITS_PROPERTY_KEY = "timestampBits";
    private static final String WORKERIDBITS_PROPERTY_KEY = "workerIdBits";
    private static final String SEQUENCEBITS_PROPERTY_KEY = "sequenceBits";
    private static final String SEQUENCEINITRANGE_PROPERTY_KEY = "sequenceInitRange";
    private static final String DATEREFERENCE_PROPERTY_KEY = "dateReference";
    private static final String IDREFERENCE_PROPERTY_KEY = "idReference";
    private static final int IDBITS_MAX_VALUE = 63;
    private static final int TIMESTAMPBITS_DEFAULT_VALUE = 40;
    private static final int WORKERIDBITS_DEFAULT_VALUE = 7;
    private static final int SEQUENCEBITS_DEFAULT_VALUE = 16;
    private static final int SEQUENCEINITRANGE_DEFAULT_VALUE = 128;
    private static final long IDREFERENCE_DEFAULT_VALUE = 0;
    private static final String DATEREFERENCE_DEFAULT_VALUE = "2018-08-08 00:00:00";
    public static final String DATEREFERENCE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private long workerId;
    private int timestampBits = TIMESTAMPBITS_DEFAULT_VALUE;
    private int workerIdBits = WORKERIDBITS_DEFAULT_VALUE;
    private int sequenceBits = SEQUENCEBITS_DEFAULT_VALUE;
    private int timestampShift;
    private int workerIdShift;
    private long maxTimestamp;
    private long maxWorkerId;
    private long sequenceMask;
    private int sequenceInitRange = SEQUENCEINITRANGE_DEFAULT_VALUE;
    private long idReference = IDREFERENCE_DEFAULT_VALUE;
    private String dateReference = DATEREFERENCE_DEFAULT_VALUE;
    private long timestampReference;

    public void load(Map<String, String> properties) {
        try {
            timestampBits = PropertiesParser.parseInt(properties, TIMESTAMPBITS_PROPERTY_KEY);
        } catch (Throwable t) {
            LOGGER.info("[timestampBits] invalid, use default value: {}", TIMESTAMPBITS_DEFAULT_VALUE);
            LOGGER.info(t.getMessage(), t);
        }
        try {
            workerIdBits = PropertiesParser.parseInt(properties, WORKERIDBITS_PROPERTY_KEY);
        } catch (Throwable t) {
            LOGGER.info("[workerIdBits] invalid, use default value: {}", WORKERIDBITS_DEFAULT_VALUE);
            LOGGER.info(t.getMessage(), t);
        }
        try {
            sequenceBits = PropertiesParser.parseInt(properties, SEQUENCEBITS_PROPERTY_KEY);
        } catch (Throwable t) {
            LOGGER.info("[sequenceBits] invalid, use default value: {}", SEQUENCEBITS_DEFAULT_VALUE);
            LOGGER.info(t.getMessage(), t);
        }
        try {
            sequenceInitRange = PropertiesParser.parseInt(properties, SEQUENCEINITRANGE_PROPERTY_KEY);
        } catch (Throwable t) {
            LOGGER.info("[sequenceInitRange] invalid, use default value: {}", SEQUENCEINITRANGE_DEFAULT_VALUE);
            LOGGER.info(t.getMessage(), t);
        }
        try {
            idReference = PropertiesParser.parseLong(properties, IDREFERENCE_PROPERTY_KEY);
            idReference = (idReference < 0) ? IDREFERENCE_DEFAULT_VALUE : idReference;
        } catch (Throwable t) {
            LOGGER.info("[idReference] invalid, use default value: {}", IDREFERENCE_DEFAULT_VALUE);
            LOGGER.info(t.getMessage(), t);
        }
        try {
            dateReference = PropertiesParser.parseDateString(properties, DATEREFERENCE_PROPERTY_KEY);
        } catch (Throwable t) {
            LOGGER.info("[dateReference] invalid, use default value: {}", DATEREFERENCE_DEFAULT_VALUE);
            LOGGER.info(t.getMessage(), t);
        }

        timestampShift = workerIdBits + sequenceBits;
        workerIdShift = sequenceBits;
        maxTimestamp = ~(-1L << timestampBits);
        maxWorkerId = ~(-1L << workerIdBits);
        sequenceMask = ~(-1L << sequenceBits);
        timestampReference = parseTimestamp(dateReference, idReference);

        validate();
    }

    private long parseTimestamp(String dateReference, long idReference) {
        long timestamp1 = System.currentTimeMillis() - 1;
        SimpleDateFormat sdf = new SimpleDateFormat(DATEREFERENCE_FORMAT);
        try {
            timestamp1 = sdf.parse(dateReference).getTime();
        } catch (ParseException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        long timestamp2 = System.currentTimeMillis() - (idReference >> timestampShift) - 1;

        return ((timestamp1 < timestamp2) ? timestamp1 : timestamp2);
    }

    private void validate() {
        String msg = null;

        if (timestampBits <= 0) {
            msg = "[timestampBits] should be positive";
        } else if (workerIdBits <= 0) {
            msg = "[workerIdBits] should be positive";
        } else if (sequenceBits <= 0) {
            msg = "[sequenceBits] should be positive";
        } else if (timestampBits + workerIdBits + sequenceBits > IDBITS_MAX_VALUE) {
            msg = String.format("Total id bits should not be greater than %d", IDBITS_MAX_VALUE);
        } else if (workerId > maxWorkerId) {
            msg = "[workerId] overflowed";
        } else if (sequenceInitRange > sequenceMask + 1) {
            msg = "[sequenceInitRange] overflowed";
        } else if (timestampReference > System.currentTimeMillis()) {
            msg = "[timestampReference] exceeded the upper limit";
        } else if (System.currentTimeMillis() - timestampReference > maxTimestamp) {
            msg = "[timestampReference] exceeded the lower limit";
        }

        if (msg != null) {
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public long getWorkerId() {
        return workerId;
    }

    public int getTimestampShift() {
        return timestampShift;
    }

    public int getWorkerIdShift() {
        return workerIdShift;
    }

    public long getMaxTimestamp() {
        return maxTimestamp;
    }

    public long getSequenceMask() {
        return sequenceMask;
    }

    public int getSequenceInitRange() {
        return sequenceInitRange;
    }

    public long getTimestampReference() {
        return timestampReference;
    }

}
