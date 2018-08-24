package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.framework.idgen.server.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class CtripServerConfig implements ServerConfig, ConfigConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripServerConfig.class);

    private long workerId;
    private int timestampBits = TIMESTAMPBITS_DEFAULT_VALUE;
    private int workerIdBits = WORKERIDBITS_DEFAULT_VALUE;
    private int sequenceBits = SEQUENCEBITS_DEFAULT_VALUE;
    private int timestampShift;
    private int workerIdShift;
    private long timestampMask;
    private long maxWorkerId;
    private long sequenceMask;
    private int sequenceInitRange = SEQUENCEINITRANGE_DEFAULT_VALUE;
    private String dateReference = DATEREFERENCE_DEFAULT_VALUE;
    private long timestampReference;

    public void importConfig(Map<String, String> properties) {
        try {
            timestampBits = PropertiesParser.parseInt(properties, TIMESTAMPBITS_PROPERTY_KEY);
        } catch (Throwable t) {
            LOGGER.info("[timestampBits] invalid, use default value");
        }
        try {
            workerIdBits = PropertiesParser.parseInt(properties, WORKERIDBITS_PROPERTY_KEY);
        } catch (Throwable t) {
            LOGGER.info("[workerIdBits] invalid, use default value");
        }
        try {
            sequenceBits = PropertiesParser.parseInt(properties, SEQUENCEBITS_PROPERTY_KEY);
        } catch (Throwable t) {
            LOGGER.info("[sequenceBits] invalid, use default value");
        }
        try {
            sequenceInitRange = PropertiesParser.parseInt(properties, SEQUENCEINITRANGE_PROPERTY_KEY);
        } catch (Throwable t) {
            LOGGER.info("[sequenceInitRange] invalid, use default value");
        }
        try {
            dateReference = PropertiesParser.parseDateString(properties, DATEREFERENCE_PROPERTY_KEY);
        } catch (Throwable t) {
            LOGGER.info("[dateReference] invalid, use default value");
        }

        workerId = parseWorkerId(properties);
        timestampShift = workerIdBits + sequenceBits;
        workerIdShift = sequenceBits;
        timestampMask = ~(-1L << timestampBits);
        maxWorkerId = ~(-1L << workerIdBits);
        sequenceMask = ~(-1L << sequenceBits);
        timestampReference = parseTimestamp(dateReference);

        validateConfig();
    }

    private long parseWorkerId(Map<String, String> properties) {
        if (checkWorkerIdDuplication(properties)) {
            LOGGER.error("[workerId] duplicated");
            throw new RuntimeException("[workerId] duplicated");
        }
        try {
            return PropertiesParser.parseLong(properties, getWorkerIdPropertyKey());
        } catch (Throwable t) {
            LOGGER.error("[workerId] invalid", t);
            throw t;
        }
    }

    private boolean checkWorkerIdDuplication(Map<String, String> properties) {
        return false;
    }

    private String getWorkerIdPropertyKey() {
        return String.format(WORKERID_PROPERTY_KEY_PATTERN, getWorkerIdPropertyKeySuffix());
    }

    private String getWorkerIdPropertyKeySuffix() {
        try {
            return Foundation.net().getHostAddress();
        } catch (Throwable t) {
            throw new RuntimeException("Get local IP failed", t);
        }
    }

    private long parseTimestamp(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATEREFERENCE_FORMAT);
        try {
            return sdf.parse(date).getTime();
        } catch (ParseException e) {
            LOGGER.error("[timestampReference] failed", e);
            throw new RuntimeException("[timestampReference] failed", e);
        }
    }

    private void validateConfig() {
        String errMsg = null;

        if (timestampBits <= 0) {
            errMsg = "[timestampBits] not positive";
        } else if (workerIdBits <= 0) {
            errMsg = "[workerIdBits] not positive";
        } else if (sequenceBits <= 0) {
            errMsg = "[sequenceBits] not positive";
        } else if (timestampBits + workerIdBits + sequenceBits > IDBITS_MAX_VALUE) {
            errMsg = "idBits overflow";
        } else if (workerId < 0) {
            errMsg = "[workerId] negative";
        } else if (workerId > maxWorkerId) {
            errMsg = "[workerId] overflowed";
        } else if (sequenceInitRange > sequenceMask + 1) {
            errMsg = "[sequenceInitRange] overflowed";
        } else if (timestampReference > System.currentTimeMillis()) {
            errMsg = "[timestampReference] too late";
        } else if (System.currentTimeMillis() - timestampReference > timestampMask) {
            errMsg = "[timestampReference] too early";
        }

        if (errMsg != null) {
            LOGGER.error(errMsg);
            throw new RuntimeException(errMsg);
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

    public long getTimestampMask() {
        return timestampMask;
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
