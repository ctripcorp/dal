package com.ctrip.platform.dal.dao.tvp;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TVPMetaInfo {
    private ConcurrentMap<String, List<String>> map = new ConcurrentHashMap<>();
    private AtomicBoolean metaInfoFetched = new AtomicBoolean(false);

    public List<String> getTVPColumns(String tvpName) {
        return map.get(tvpName);
    }

    public void setTVPColumns(String tvpName, List<String> columns) {
        map.putIfAbsent(tvpName, columns);
    }

    public Boolean getMetaInfoFetched() {
        return metaInfoFetched.get();
    }

    public void setMetaInfoFetched() {
        metaInfoFetched.compareAndSet(false, true);
    }

}
