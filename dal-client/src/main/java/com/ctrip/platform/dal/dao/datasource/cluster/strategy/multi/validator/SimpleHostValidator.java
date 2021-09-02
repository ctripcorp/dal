package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

/**
 * add to black list when throw exception and validate hosts in pre and black list
 * @Author limingdong
 * @create 2021/8/19
 */
public class SimpleHostValidator extends AbstractHostValidator implements HostValidator {

    private String INIT_SQL = "select 1";

    public SimpleHostValidator(Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        super(configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    @Override
    public boolean validate(HostConnection connection) {
        if (connection != null && connection.getHost() != null) {
            addToBlackAndRemoveFromPre(connection.getHost());
        }
        return true;
    }

    protected boolean doValidate(Connection connection) {
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
    public void triggerValidate() {
        asyncValidate(getAllBlackListNodes());
    }

    @Override
    protected boolean startFixedScheduler() {
        return false;
    }

    @Override
    protected String getCatLogType() {
        return "DAL.OB";
    }
}
