package com.ctrip.platform.idgen.config;

public interface Constants {

    int ID_MAX_BITS = 64 - 1;

    String WORKERID_PROPERTY_KEY_PATTERN = "workerId_%s";
    String TIMESTAMPBITS_PROPERTY_KEY = "timestampBits";
    String WORKERIDBITS_PROPERTY_KEY = "workerIdBits";
    String SEQUENCEBITS_PROPERTY_KEY = "sequenceBits";
    String SEQUENCEINITRANGE_PROPERTY_KEY = "sequenceInitRange";
    String TIMESTAMPREFERENCE_PROPERTY_KEY = "dateReference";
    String TIMESTAMPREFERENCE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String SEQUENCENAME_REGISTER_STATE_ON = "on";

}
