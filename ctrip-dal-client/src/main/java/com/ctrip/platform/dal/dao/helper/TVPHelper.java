package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.tvp.TVPMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TVPHelper {
    private ConcurrentMap<String, TVPMetaInfo> tvpColumnsMap = new ConcurrentHashMap<>();
    private final Object LOCK = new Object();

    public List<String> getTVPColumns(String logicDbName, String tvpName, Map<String, Integer> columnTypes,
            DalClient client) {
        if (logicDbName == null || logicDbName.isEmpty())
            return null;

        if (tvpName == null || tvpName.isEmpty())
            return null;

        if (client == null)
            return null;

        TVPMetaInfo metaInfo = tvpColumnsMap.get(logicDbName);
        if (metaInfo == null) {
            synchronized (LOCK) {
                metaInfo = tvpColumnsMap.get(logicDbName);
                if (metaInfo == null) {
                    metaInfo = new TVPMetaInfo();
                    tvpColumnsMap.putIfAbsent(logicDbName, metaInfo);
                }
            }
        }

        return metaInfo.getTVPColumns(tvpName, columnTypes, client);
    }

}
