package com.ctrip.framework.idgen.client.log;

public interface CatConstants {

    String TYPE_ROOT = "IdGen.Client";
    String TYPE_VERSION = TYPE_ROOT + ".version";
    String TYPE_CREATE = TYPE_ROOT + ".create";
    String TYPE_NEXT_ID = TYPE_ROOT + ".nextId";
    String TYPE_PREFETCH = TYPE_ROOT + ".prefetch";
    String TYPE_ACTIVE_FETCH = TYPE_ROOT + ".activeFetch";
    String TYPE_CALL_SERVICE = TYPE_ROOT + ".callService";
    String TYPE_ID_POOL = TYPE_ROOT + ".pool";

}
