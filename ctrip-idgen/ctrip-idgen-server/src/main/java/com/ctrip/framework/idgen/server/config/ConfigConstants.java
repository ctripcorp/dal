package com.ctrip.framework.idgen.server.config;

public interface ConfigConstants {

    String TIMESTAMPBITS_PROPERTY_KEY = "timestampBits";
    String WORKERIDBITS_PROPERTY_KEY = "workerIdBits";
    String SEQUENCEBITS_PROPERTY_KEY = "sequenceBits";
    String SEQUENCEINITRANGE_PROPERTY_KEY = "sequenceInitRange";
    String DATEREFERENCE_PROPERTY_KEY = "dateReference";

    String WORKERID_PROPERTY_KEY_PATTERN = "workerId_%s";
    String DATEREFERENCE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String WHITELIST_ENABLED_FLAG = "on";

    int IDBITS_MAX_VALUE = 63;
    int TIMESTAMPBITS_DEFAULT_VALUE = 40;
    int WORKERIDBITS_DEFAULT_VALUE = 7;
    int SEQUENCEBITS_DEFAULT_VALUE = 16;
    int SEQUENCEINITRANGE_DEFAULT_VALUE = 128;
    String DATEREFERENCE_DEFAULT_VALUE = "2018-08-08 00:00:00";

    int REQUESTSIZE_MAX_VALUE = 5000;
    int TIMEOUTMILLIS_MAX_VALUE = 10;
    int TIMEOUTMILLIS_DEFAULT_VALUE = 2;

}
