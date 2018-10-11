package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.exception.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Map;

public class CtripSnowflakeConfig implements SnowflakeConfig<Map<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripSnowflakeConfig.class);

    private static final String TIMESTAMP_BITS_PROPERTY_KEY = "timestampBits";
    private static final String WORKER_ID_BITS_PROPERTY_KEY = "workerIdBits";
    private static final String SEQUENCE_BITS_PROPERTY_KEY = "sequenceBits";
    private static final String ID_REFERENCE_PROPERTY_KEY = "idReference";
    private static final String DATE_REFERENCE_PROPERTY_KEY = "dateReference";
    private static final String SEQUENCE_RESET_RANGE_PROPERTY_KEY = "sequenceResetRange";

    private static final int ID_BITS_MAX_VALUE = 63;
    private static final int TIMESTAMP_BITS_DEFAULT_VALUE = 40;
    private static final int WORKER_ID_BITS_DEFAULT_VALUE = 7;
    private static final int SEQUENCE_BITS_DEFAULT_VALUE = 16;
    private static final long ID_REFERENCE_DEFAULT_VALUE = 0;
    private static final String DATE_REFERENCE_DEFAULT_VALUE = "2018-08-08 00:00:00";
    private static final String DATE_REFERENCE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final int SEQUENCE_RESET_RANGE_DEFAULT_VALUE = 128;

    // Originally configured parameters
    private int timestampBits;
    private int workerIdBits;
    private int sequenceBits;
    private long idReference;
    private String dateReference = DATE_REFERENCE_DEFAULT_VALUE;
    private int sequenceResetRange;

    // Computed parameters
    private int timestampShift;
    private int workerIdShift;
    private long maxTimestamp;
    private long maxWorkerId;
    private long sequenceMask;
    private long timestampReference;

    private Server server;
    private SnowflakeConfig defaultConfig;

    public CtripSnowflakeConfig(Server server) {
        this(server, null);
    }

    public CtripSnowflakeConfig(Server server, SnowflakeConfig defaultConfig) {
        this.server = server;
        this.defaultConfig = defaultConfig;
    }

    public void load(Map<String, String> properties) {
        loadDefault();
        loadProperties(properties);
        timestampShift = workerIdBits + sequenceBits;
        workerIdShift = sequenceBits;
        maxTimestamp = ~(-1L << timestampBits);
        maxWorkerId = ~(-1L << workerIdBits);
        sequenceMask = ~(-1L << sequenceBits);
        timestampReference = parseTimestampReference(idReference, dateReference);
        validate();
    }

    private void loadDefault() {
        if (defaultConfig != null) {
            timestampBits = defaultConfig.getTimestampBits();
            workerIdBits = defaultConfig.getWorkerIdBits();
            sequenceBits = defaultConfig.getSequenceBits();
            idReference = defaultConfig.getIdReference();
            dateReference = defaultConfig.getDateReference();
            sequenceResetRange = defaultConfig.getSequenceResetRange();
        } else {
            timestampBits = TIMESTAMP_BITS_DEFAULT_VALUE;
            workerIdBits = WORKER_ID_BITS_DEFAULT_VALUE;
            sequenceBits = SEQUENCE_BITS_DEFAULT_VALUE;
            idReference = ID_REFERENCE_DEFAULT_VALUE;
            dateReference = DATE_REFERENCE_DEFAULT_VALUE;
            sequenceResetRange = SEQUENCE_RESET_RANGE_DEFAULT_VALUE;
        }
    }

    private void loadProperties(Map<String, String> properties) {
        try {
            timestampBits = Integer.parseInt(properties.get(TIMESTAMP_BITS_PROPERTY_KEY));
        } catch (Exception e) {
            LOGGER.info("[timestampBits] invalid, use default value: {}", timestampBits, e);
        }
        try {
            workerIdBits = Integer.parseInt(properties.get(WORKER_ID_BITS_PROPERTY_KEY));
        } catch (Exception e) {
            LOGGER.info("[workerIdBits] invalid, use default value: {}", workerIdBits, e);
        }
        try {
            sequenceBits = Integer.parseInt(properties.get(SEQUENCE_BITS_PROPERTY_KEY));
        } catch (Exception e) {
            LOGGER.info("[sequenceBits] invalid, use default value: {}", sequenceBits, e);
        }
        try {
            idReference = Long.parseLong(properties.get(ID_REFERENCE_PROPERTY_KEY));
        } catch (Exception e) {
            LOGGER.info("[idReference] invalid, use default value: {}", idReference, e);
        }
        try {
            String dateString = properties.get(DATE_REFERENCE_PROPERTY_KEY);
            new SimpleDateFormat(DATE_REFERENCE_FORMAT).parse(dateString);
            dateReference = dateString;
        } catch (Exception e) {
            LOGGER.info("[dateReference] invalid, use default value: '{}'", dateReference, e);
        }
        try {
            sequenceResetRange = Integer.parseInt(properties.get(SEQUENCE_RESET_RANGE_PROPERTY_KEY));
        } catch (Exception e) {
            LOGGER.info("[sequenceResetRange] invalid, use default value: {}", sequenceResetRange, e);
        }
    }

    private long parseTimestampReference(long idReference, String dateReference) {
        idReference = idReference < 0 ? 0 : idReference;
        long timestampRefToId = System.currentTimeMillis() - (idReference >> timestampShift) - 1;

        long timestampRefToDate = System.currentTimeMillis() - 1;
        try {
            timestampRefToDate = new SimpleDateFormat(DATE_REFERENCE_FORMAT).parse(dateReference).getTime();
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception", e);
        }

        return (timestampRefToId < timestampRefToDate) ? timestampRefToId : timestampRefToDate;
    }

    private void validate() {
        String msg = null;
        if (timestampBits <= 0) {
            msg = "[timestampBits] should be positive";
        } else if (workerIdBits <= 0) {
            msg = "[workerIdBits] should be positive";
        } else if (sequenceBits <= 0) {
            msg = "[sequenceBits] should be positive";
        } else if (timestampBits + workerIdBits + sequenceBits > ID_BITS_MAX_VALUE) {
            msg = "Total id bits overflowed";
        } else if (server.getWorkerId() > maxWorkerId) {
            msg = "[workerId] overflowed";
        } else if (sequenceResetRange > sequenceMask + 1) {
            msg = "[sequenceResetRange] overflowed";
        } else if (timestampReference > System.currentTimeMillis()) {
            msg = "[timestampReference] exceeded the upper limit";
        } else if (System.currentTimeMillis() - timestampReference > maxTimestamp) {
            msg = "[timestampReference] exceeded the lower limit";
        }
        if (msg != null) {
            LOGGER.error(msg);
            throw new InvalidParameterException(msg);
        }
    }

    @Override
    public String toString() {
        return String.format("timestampBits: %d, workerIdBits: %d, sequenceBits: %d, idReference: %d, " +
                "dateReference: '%s', sequenceResetRange: %d", timestampBits, workerIdBits, sequenceBits,
                idReference, dateReference, sequenceResetRange);
    }

    @Override
    public boolean differs(SnowflakeConfig another) {
        if (null == another) {
            return true;
        }
        return (this.timestampBits != another.getTimestampBits() ||
                this.workerIdBits != another.getWorkerIdBits() ||
                this.sequenceBits != another.getSequenceBits() ||
                this.idReference != another.getIdReference() ||
                !this.dateReference.equals(another.getDateReference()) ||
                this.sequenceResetRange != another.getSequenceResetRange());
    }

    @Override
    public long getWorkerId() {
        return server.getWorkerId();
    }

    @Override
    public int getTimestampBits() {
        return timestampBits;
    }

    @Override
    public int getWorkerIdBits() {
        return workerIdBits;
    }

    @Override
    public int getSequenceBits() {
        return sequenceBits;
    }

    @Override
    public long getIdReference() {
        return idReference;
    }

    @Override
    public String getDateReference() {
        return dateReference;
    }

    @Override
    public int getSequenceResetRange() {
        return sequenceResetRange;
    }

    @Override
    public int getTimestampShift() {
        return timestampShift;
    }

    @Override
    public int getWorkerIdShift() {
        return workerIdShift;
    }

    @Override
    public long getMaxTimestamp() {
        return maxTimestamp;
    }

    @Override
    public long getSequenceMask() {
        return sequenceMask;
    }

    @Override
    public long getTimestampReference() {
        return timestampReference;
    }

}
