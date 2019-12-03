package com.ctrip.datasource.configure;

import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.datasource.util.GsonUtils;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.io.IOException;
import java.util.HashMap;

public class CtripClusterInfoProvider implements ClusterInfoProvider {

    private DalPropertiesManager manager;
    private HttpExecutor executor;

    public CtripClusterInfoProvider(DalPropertiesManager manager, HttpExecutor executor) {
        this.manager = manager;
        this.executor = executor;
    }

    // TODO: cache

    @Override
    public ClusterInfo getClusterInfo(String titanKey) {
        try {
            String url = String.format(manager.getDalPropertiesLocator().getClusterInfoQueryUrl(), titanKey, "dal-client");
            String res = executor.executeGet(url, new HashMap<>(), 5000);
            ClusterInfoResponseEntity response = GsonUtils.json2T(res, ClusterInfoResponseEntity.class);
            if (response != null && response.getStatus() == 200) {
                return response.getClusterInfo();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new DalRuntimeException(String.format("failed to get cluster info for titan key '%s'", titanKey), e);
        }
    }

}
