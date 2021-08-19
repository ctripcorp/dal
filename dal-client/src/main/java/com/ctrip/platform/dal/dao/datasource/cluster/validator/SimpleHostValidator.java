package com.ctrip.platform.dal.dao.datasource.cluster.validator;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/19
 */
public class SimpleHostValidator extends AbstractHostValidator implements HostValidator  {

    public SimpleHostValidator(ConnectionFactory factory, Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        super(factory, configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    @Override
    public boolean validate(HostConnection connection) {
        return true;
    }

    @Override
    protected void doAsyncValidate(HostSpec host) {
        try (Connection ignored = getConnection(host)){
            removeFromAllBlackList(host);
            LOGGER.info(ASYNC_VALIDATE_RESULT + "OK");
        } catch (Throwable e) {
            addToBlackAndRemoveFromPre(host);
            LOGGER.warn(CAT_LOG_TYPE, e);
        }
    }

    @Override
    protected String getCatLogType() {
        return "DAL." + ClusterType.OB.getValue();
    }
}
