package com.ctrip.datasource.configure;

import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.datasource.util.GsonUtils;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.NullClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CtripClusterInfoProvider implements ClusterInfoProvider {

    private DalPropertiesLocator locator;
    private HttpExecutor executor;
    private final Map<String, ClusterInfo> clusterInfoCache = new ConcurrentHashMap<>();

    public CtripClusterInfoProvider(DalPropertiesLocator locator, HttpExecutor executor) {
        this.locator = locator;
        this.executor = executor;
    }

    @Override
    public ClusterInfo getClusterInfo(String titanKey) {
        ClusterInfo clusterInfo = clusterInfoCache.get(titanKey);
        if (clusterInfo == null) {
            synchronized (clusterInfoCache) {
                clusterInfo = clusterInfoCache.get(titanKey);
                if (clusterInfo == null) {
                    clusterInfo = getLatestClusterInfo(titanKey);
                    clusterInfoCache.put(titanKey, clusterInfo);
                }
            }
        }
        return clusterInfo;
    }

    private ClusterInfo getLatestClusterInfo(String titanKey) {
        ClusterInfo clusterInfo = null;
        try {
            String url = String.format(locator.getClusterInfoQueryUrl(), titanKey, "dal-client");
            String res = executor.executeGet(url, new HashMap<>(), 5000);
            ClusterInfoResponseEntity response = GsonUtils.json2T(res, ClusterInfoResponseEntity.class);
            if (response != null && response.getStatus() == 200) {
                clusterInfo = response.getClusterInfo();
            }
            return clusterInfo != null ? clusterInfo : new NullClusterInfo();
        } catch (IOException e) {
            e.printStackTrace();
            throw new DalRuntimeException(String.format("failed to get cluster info for titan key '%s'", titanKey), e);
        }
    }

}
