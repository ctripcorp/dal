package com.ctrip.framework.idgen.client.constant;

import com.dianping.cat.message.Transaction;

public interface CatConstants {

    String TYPE_ROOT = "IdGen.Client";
    String TYPE_VERSION = TYPE_ROOT + ".version";
    String TYPE_ID_GENERATOR_FACTORY = TYPE_ROOT + ".idGeneratorFactory";
    String TYPE_STATIC_GENERATOR = TYPE_ROOT + ".staticGenerator";
    String TYPE_DYNAMIC_GENERATOR = TYPE_ROOT + ".dynamicGenerator";
    String TYPE_CALL_SERVICE = TYPE_ROOT + ".callService";

    String TYPE_SEQUENCE_NAME = TYPE_ROOT + ".sequenceName";
    String TYPE_FETCH_POOL_SIZE = TYPE_ROOT + ".fetchPoolSize";
    String TYPE_REMAINED_POOL_SIZE = TYPE_ROOT + ".remainedPoolSize";
    String TYPE_POOL_QPS = TYPE_ROOT + ".poolQps";
    String TYPE_ACTIVE_FETCH_RETRIES = TYPE_ROOT + ".activeFetchRetries";

    String STATUS_SUCCESS = Transaction.SUCCESS;
    String STATUS_NULL_RESPONSE = "null response";

}
