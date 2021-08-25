package com.ctrip.platform.dal.dao.datasource.cluster.validator;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.cluster.ClusterType;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/19
 */
public class SimpleHostValidator extends AbstractHostValidator implements HostValidator  {

    private String INIT_SQL = "select 1";

    public SimpleHostValidator(ConnectionFactory factory, Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        super(factory, configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    @Override
    public boolean validate(HostConnection connection) {
        return doValidate(connection);
    }

    private boolean doValidate(Connection connection) {
        boolean isValid;

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.setQueryTimeout(1);
            stmt.execute(INIT_SQL);
            isValid = true;
        } catch (SQLException e) {
            isValid = false;
        } finally {
            if (stmt != null)
                try {
                    stmt.close();
                } catch (Exception ignore2) {
                    /* NOOP */}
        }

        return isValid;
    }

    @Override
    protected void doAsyncValidate(HostSpec host) {
        try (Connection connection = getConnection(host)){
            if (doValidate(connection)) {
                removeFromAllBlackList(host);
            } else {
                addToBlackAndRemoveFromPre(host);
            }
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
