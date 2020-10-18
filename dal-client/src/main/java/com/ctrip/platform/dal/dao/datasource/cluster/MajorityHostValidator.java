package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.HostAndPort;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MajorityHostValidator implements ConnectionValidator, HostValidator {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CAT_LOG_TYPE = "DAL.pickConnection";
    private static final String FIND_NO_HOST_SPEC = "Validator::findNoHostSpec";
    private static final String CONNECTION_URL = "Validator::getConnectionUrl";
    private static final String BLACK_LIST = "Validator::addTo%BlackHost";
    private static final String DEFAULT = "default";
    private static final String ADD_BLACK_LIST = "BlackList::addBlackList";
    private static final String VALIDATE_RESULT_UNKNOWN = "PreBlackList::resultUnknown";

    private volatile long lastValidateSecond = 0;
    private volatile ScheduledExecutorService executorService;
    private static volatile HashMap<HostSpec, Long> hostBlackList = new HashMap<>();
    private static volatile HashMap<HostSpec, Long> preBlackList = new HashMap<>();
    private static final String validateSQL1 = "select members.MEMBER_STATE MEMBER_STATE, " +
            "members.MEMBER_ID MEMBER_ID, " +
            "member_stats.MEMBER_ID CURRENT_MEMBER_ID " +
            "from performance_schema.replication_group_members members left join performance_schema.replication_group_member_stats member_stats on member_stats.MEMBER_ID=members.MEMBER_ID;";

    private enum MemberState{
        Online, Error, Offline, Recovering;
    }

    private enum Columns {
        MEMBER_STATE, MEMBER_ID, CURRENT_MEMBER_ID;
    }

    private enum Result {
        OK, FAILED, UNKNOWN
    }

    public MajorityHostValidator() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        lastValidateSecond = System.currentTimeMillis() / 1000;
    }

    @Override
    public boolean available(ConnectionFactory factory, HostSpec host, RouteOptions options) {
        long failOverTime = options.failoverTime();
        long blackListTimeOut = options.blacklistTimeout();
        Set<HostSpec> configuredHosts = options.configuredHosts();

        if (hostBlackList.containsKey(host) && hostBlackList.get(host) > System.currentTimeMillis() - blackListTimeOut) {
            return false;
        }

        if (preBlackList.containsKey(host)) {
            validateWithNewConnection(factory, host, configuredHosts.size());

             if (preBlackList.get(host) < System.currentTimeMillis() - failOverTime) {
                 return false;
             }
        }

        return true;
    }

    @Override
    public boolean validate(Connection connection, Set<HostSpec> configuredHosts) throws SQLException {
        HostSpec currentHost = getHostSpecFromConnection(connection);
        return validateAndUpdate(connection, currentHost, configuredHosts.size());
    }

    private HostSpec getHostSpecFromConnection(Connection connection) {
        String urlForLog = null;
        try {
            urlForLog = connection.getMetaData().getURL();
        } catch (SQLException e) {
            LOGGER.error(CONNECTION_URL, e);
            return null;
        }

        HostAndPort hostAndPort = ConnectionStringParser.parseHostPortFromURL(urlForLog);
        if (StringUtils.isEmpty(hostAndPort.getHost()) || hostAndPort.getPort() == null) {
            LOGGER.logEvent(CAT_LOG_TYPE, FIND_NO_HOST_SPEC, urlForLog);
            return null;
        }

        return new HostSpec(hostAndPort.getHost(), hostAndPort.getPort(), DEFAULT);
    }

    private boolean validateAndUpdate(Connection connection, HostSpec currentHost, int clusterHostCount) throws SQLException {
        try {
            if (validate(connection, clusterHostCount)) {
                removeFromPreBlackList(currentHost);
                return true;
            } else {
                addToBlackList(currentHost);
                removeFromPreBlackList(currentHost);
                return false;
            }
        } catch (SQLException e) {
            addToPreBlackList(currentHost);
            throw e;
        }
    }

    private void validateWithNewConnection(ConnectionFactory factory, HostSpec host, int clusterHostCount) {
        long currentSecond = System.currentTimeMillis() / 1000;
        //TODO 异步创建线程，并执行validate操作
        synchronized (this) {
            if (this.lastValidateSecond == currentSecond) {
                return;
            }
        }

        this.lastValidateSecond = currentSecond;
        executorService.schedule(() -> {

            try (Connection connection = factory.createConnectionForHost(host)){

                validateAndUpdate(connection, host, clusterHostCount);
            }catch (Throwable e) {
                LOGGER.error(CAT_LOG_TYPE, e);
            }
        }, 1, TimeUnit.MILLISECONDS);
    }

    private boolean validate(Connection connection, int clusterHostCount) throws SQLException {
        boolean currentHostState = false;
        int onlineCount = 0;

        try(Statement statement = connection.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery(validateSQL1)) {
                while (resultSet.next()) {
                    String memberId = resultSet.getString(Columns.MEMBER_ID.name());
                    String currentMemberId = resultSet.getString(Columns.CURRENT_MEMBER_ID.name());
                    String memberState = resultSet.getString(Columns.MEMBER_STATE.name());
                    if (memberId.equals(currentMemberId)) {
                        currentHostState = MemberState.Online.name().equalsIgnoreCase(memberState);
                    }
                    if (MemberState.Online.name().equalsIgnoreCase(memberState)) {
                        onlineCount++;
                    }
                }
            }
        }

        return currentHostState && 2 * onlineCount > clusterHostCount;
    }

    private void addToPreBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            LOGGER.warn(String.format(BLACK_LIST, "Pre"));
            return;
        }

        synchronized (MajorityHostValidator.class) {
            LOGGER.logEvent(CAT_LOG_TYPE, VALIDATE_RESULT_UNKNOWN, hostSpec.toString());
            if (!preBlackList.containsKey(hostSpec)) {
                Long currentTime = System.currentTimeMillis();
                preBlackList.put(hostSpec, currentTime);
            }
        }
    }

    private void addToBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            LOGGER.warn(String.format(BLACK_LIST, ""));
            return;
        }

        synchronized (MajorityHostValidator.class) {
            LOGGER.logEvent(CAT_LOG_TYPE, ADD_BLACK_LIST, hostSpec.toString());
            Long currentTime = System.currentTimeMillis();
            hostBlackList.put(hostSpec, currentTime);
        }
    }

    private void removeFromPreBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        synchronized (MajorityHostValidator.class) {
            if (preBlackList.containsKey(hostSpec)) {
                preBlackList.remove(hostSpec);
            }
        }
    }

}
