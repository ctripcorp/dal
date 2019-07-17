package com.ctrip.platform.dal.daogen.DynamicDS;

import com.ctrip.platform.dal.daogen.DalDynamicDSDao;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by taochen on 2019/7/17.
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int MAX_CACHE_SIZE;

    public LRUCache(int cacheSize) {
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        MAX_CACHE_SIZE = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean isRemove = size() > MAX_CACHE_SIZE;
        if (isRemove) {
            DalDynamicDSDao dalDynamicDSDao = DalDynamicDSDao.getInstance();
            dalDynamicDSDao.removeLoadingCache((String)eldest.getKey());
        }
        return isRemove;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, V> entry : entrySet()) {
            sb.append(String.format("%s:%s ", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}
