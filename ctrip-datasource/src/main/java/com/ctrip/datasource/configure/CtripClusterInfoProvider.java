package com.ctrip.datasource.configure;

import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.ClusterInfoProvider;
import com.ctrip.platform.dal.dao.configure.NullClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.helper.JsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CtripClusterInfoProvider implements ClusterInfoProvider {

    private static final int DEFAULT_HTTP_TIMEOUT_MS = 1800;
    private static final String CAT_LOG_TYPE = "DAL.configure";
    private static final String CAT_LOG_NAME_FORMAT = "GetClusterInfo:%s";

    private DalPropertiesLocator locator;
    private HttpExecutor executor;
    private static final Map<String, ClusterInfo> clusterInfoCache = new ConcurrentHashMap<>();

    public CtripClusterInfoProvider(DalPropertiesLocator locator, HttpExecutor executor) {
        this.locator = locator;
        this.executor = executor;
    }

    @Override
    public ClusterInfo getClusterInfo(String titanKey) {
        String key = titanKey.toLowerCase();
        ClusterInfo clusterInfo = clusterInfoCache.get(key);
        if (clusterInfo == null) {
            synchronized (clusterInfoCache) {
                clusterInfo = clusterInfoCache.get(key);
                if (clusterInfo == null) {
                    clusterInfo = getLatestClusterInfo(titanKey);
                    clusterInfoCache.put(key, clusterInfo);
                }
            }
        }
        return clusterInfo;
    }

    private ClusterInfo getLatestClusterInfo(String titanKey) {
        String clusterInfoQueryUrl = locator.getClusterInfoQueryUrl();
        if (StringUtils.isEmpty(clusterInfoQueryUrl)) {
            Cat.logEvent(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_FORMAT, "SKIP:" + titanKey),
                    Event.SUCCESS, "empty url");
            return new NullClusterInfo();
        }

        ClusterInfo clusterInfo = null;
        Transaction transaction = Cat.newTransaction(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_FORMAT, titanKey));
        try {
            String subEnv = Foundation.server().getSubEnv();
            if (subEnv != null && subEnv.toLowerCase().contains("aws")) {
                Cat.logEvent(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_FORMAT, "SKIP:" + titanKey),
                        Event.SUCCESS, subEnv);
                return new NullClusterInfo();
            }

            String url = String.format(clusterInfoQueryUrl, titanKey);
            String appId = Foundation.app().getAppId();
            if (!StringUtils.isEmpty(appId))
                url = url + "&app=" + appId;
            String res = executor.executeGet(url, new HashMap<>(), DEFAULT_HTTP_TIMEOUT_MS);
            ClusterInfoResponseEntity response = JsonUtils.fromJson(res, ClusterInfoResponseEntity.class);
            if (response != null && response.getStatus() == 200)
                clusterInfo = response.getClusterInfo();
            if (clusterInfo == null)
                clusterInfo = new NullClusterInfo();
            Cat.logEvent(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_FORMAT, titanKey), Event.SUCCESS, clusterInfo.toString());
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable t) {
            Cat.logEvent(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_FORMAT, "EXCEPTION:" + titanKey), Event.SUCCESS, t.getMessage());
            transaction.setStatus(t);
        } finally {
            transaction.complete();
        }

        return clusterInfo != null ? clusterInfo : new NullClusterInfo();
    }

}
