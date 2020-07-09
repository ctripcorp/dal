package com.ctrip.datasource.configure;

import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.datasource.util.Version;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.ClusterInfoProvider;
import com.ctrip.platform.dal.dao.configure.NullClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
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
    private static final String CAT_LOG_NAME_FORMAT = "Cluster::getClusterInfo:%s";

    private DalPropertiesLocator locator;
    private HttpExecutor executor;
    private static final Map<String, ClusterInfo> clusterInfoCache = new ConcurrentHashMap<>();
    private static final EnvUtils envUtils = DalElementFactory.DEFAULT.getEnvUtils();

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
            String url = String.format(clusterInfoQueryUrl, titanKey) + buildExtQueryString();
            String res = executor.executeGet(url, new HashMap<>(), DEFAULT_HTTP_TIMEOUT_MS);
            ClusterInfoResponseEntity response = JsonUtils.fromJson(res, ClusterInfoResponseEntity.class);
            if (response != null && response.getStatus() == 200)
                clusterInfo = response.getClusterInfo();
            if (clusterInfo == null)
                clusterInfo = new NullClusterInfo();
            if (clusterInfo instanceof NullClusterInfo)
                Cat.logEvent(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_FORMAT, "SKIP:" + titanKey),
                        Event.SUCCESS, String.format("subEnv=%s, idc=%s", envUtils.getSubEnv(), envUtils.getIdc()));
            else
                Cat.logEvent(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_FORMAT, titanKey),
                        Event.SUCCESS, clusterInfo.toString());
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable t) {
            Cat.logEvent(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_FORMAT, "EXCEPTION:" + titanKey),
                    Event.SUCCESS, t.getMessage());
            transaction.setStatus(t);
        } finally {
            transaction.complete();
        }

        return clusterInfo != null ? clusterInfo : new NullClusterInfo();
    }

    protected String buildExtQueryString() {
        StringBuilder sb = new StringBuilder();
        String version = Version.getVersion();
        if (!StringUtils.isEmpty(version) && !Version.UNKNOWN.equalsIgnoreCase(version))
            sb.append("&clientVersion=").append(version);
        String appId = Foundation.app().getAppId();
        if (!StringUtils.isEmpty(appId))
            sb.append("&appId=").append(appId);
        String subEnv = envUtils.getSubEnv();
        if (!StringUtils.isEmpty(subEnv))
            sb.append("&subEnv=").append(subEnv);
        String idc = envUtils.getIdc();
        if (!StringUtils.isEmpty(idc))
            sb.append("&idc=").append(idc);
        return sb.toString();
    }

    protected boolean checkEnv() {
        String env = envUtils.getEnv();
        String subEnv = envUtils.getSubEnv();
        String idc = envUtils.getIdc();
        boolean isPro = env != null && env.toLowerCase().contains("pro");
        return isPro ? (idc == null || !idc.toLowerCase().contains("aws")) : StringUtils.isEmpty(subEnv);
    }

}
