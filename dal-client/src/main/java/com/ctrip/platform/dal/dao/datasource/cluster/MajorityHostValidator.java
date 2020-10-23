package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.HostAndPort;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.*;

public class MajorityHostValidator implements ConnectionValidator, HostValidator {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CAT_LOG_TYPE = "DAL.pickConnection";
    private static final String FIND_NO_HOST_SPEC = "Validator::findNoHostSpec";
    private static final String CONNECTION_URL = "Validator::getConnectionUrl";
    private static final String DEFAULT = "default";
    private static final String ADD_BLACK_LIST = "Validator::addToBlackList";
    private static final String ADD_PRE_BLACK_LIST = "Validator::addToPreBlackList";
    private static final String REMOVE_BLACK_LIST = "Validator::removeFromBlackList";
    private static final String REMOVE_PRE_BLACK_LIST = "Validator::removeFromPreBlackList";
    private static final String VALIDATE_COMMAND_DENIED = "Validator::validateCommandDenied";

    private volatile long lastValidateSecond = 0;
    private volatile Set<HostSpec> configuredHosts;
    private volatile long failOverTime;
    private volatile long blackListTimeOut;
    private volatile ConnectionFactory factory;
    private static volatile ExecutorService service = Executors.newFixedThreadPool(4);
    private static volatile ConcurrentHashMap<HostSpec, Long> hostBlackList = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<HostSpec, Long> preBlackList = new ConcurrentHashMap<>();
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

    public MajorityHostValidator() {
        lastValidateSecond = System.currentTimeMillis() / 1000;
    }

    public MajorityHostValidator(ConnectionFactory factory, Set<HostSpec> configuredHosts, long failOverTime, long blackListTimeOut) {
        this();
        this.factory = factory;
        this.configuredHosts = configuredHosts;
        this.failOverTime = failOverTime;
        this.blackListTimeOut = blackListTimeOut;
    }

    @Override
    public boolean available(HostSpec host) {
        if ((hostBlackList.containsKey(host) && hostBlackList.get(host) > System.currentTimeMillis() - blackListTimeOut) ||
                (preBlackList.containsKey(host) && preBlackList.get(host) < System.currentTimeMillis() - failOverTime)) {
            return false;
        }

        return true;
    }

    @Override
    public void triggerValidate() {
        validateWithNewConnection();
    }

    @Override
    public boolean validate(Connection connection) throws SQLException {
        HostSpec currentHost = getHostSpecFromConnection(connection);
        return validateAndUpdate(connection, currentHost, configuredHosts.size());
    }

    protected HostSpec getHostSpecFromConnection(Connection connection) {
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
                removeFromAllBlackList(currentHost);
                return true;
            } else {
                addToBlackAndRemoveFromPre(currentHost);
                return false;
            }
        } catch (SQLException e) {
            addToPreAbsentAndBlackPresent(currentHost);
            throw e;
        }
    }

    private void validateWithNewConnection() {
        long currentSecond = System.currentTimeMillis() / 1000;
        synchronized (this) {
            if (this.lastValidateSecond == currentSecond) {
                return;
            }
        }

        this.lastValidateSecond = currentSecond;
        asyncValidate(preBlackList.keySet());
        asyncValidate(hostBlackList.keySet());
    }

    private void asyncValidate(Set<HostSpec> hostSpecs) {
        for (HostSpec host : hostSpecs) {
            if (configuredHosts.contains(host)) {
                service.submit(() -> {
                    try (Connection connection = factory.createConnectionForHost(host)){
                        validateAndUpdate(connection, host, configuredHosts.size());
                    }catch (Throwable e) {
                        LOGGER.error(CAT_LOG_TYPE, e);
                    }
                });
            }
        }
    }

    protected boolean validate(Connection connection, int clusterHostCount) throws SQLException {
        boolean currentHostState = false;
        int onlineCount = 0;

        try(Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(1);
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
            } catch (MySQLSyntaxErrorException e) {
                LOGGER.logEvent(CAT_LOG_TYPE, VALIDATE_COMMAND_DENIED, "");
                return true;
            }
        }

        return currentHostState && 2 * onlineCount > clusterHostCount;
    }

    private void addToPreAbsent(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        LOGGER.logEvent(CAT_LOG_TYPE, ADD_PRE_BLACK_LIST, hostSpec.toString());
        Long currentTime = System.currentTimeMillis();
        preBlackList.putIfAbsent(hostSpec, currentTime);
    }

    private void addToBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        LOGGER.logEvent(CAT_LOG_TYPE, ADD_BLACK_LIST, hostSpec.toString());
        Long currentTime = System.currentTimeMillis();
        hostBlackList.put(hostSpec, currentTime);
    }

    private void addToBlackListPresent(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        Long currentTime = System.currentTimeMillis();
        if (hostBlackList.containsKey(hostSpec)) {
            LOGGER.logEvent(CAT_LOG_TYPE, ADD_BLACK_LIST, hostSpec.toString());
            hostBlackList.put(hostSpec, currentTime);
        }
    }

    private void removeFromPreBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        Long last = preBlackList.remove(hostSpec);
        if (last != null) {
            LOGGER.logEvent(CAT_LOG_TYPE, REMOVE_PRE_BLACK_LIST, hostSpec.toString());
        }
    }

    private void removeFromBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        Long last = hostBlackList.remove(hostSpec);
        if (last != null) {
            LOGGER.logEvent(CAT_LOG_TYPE, REMOVE_BLACK_LIST, hostSpec.toString());
        }
    }

    private void addToBlackAndRemoveFromPre(HostSpec hostSpec) {
        addToBlackList(hostSpec);
        removeFromPreBlackList(hostSpec);
    }

    private void addToPreAndRemoveFromBlack(HostSpec hostSpec) {
        addToPreAbsent(hostSpec);
        removeFromBlackList(hostSpec);
    }

    private void removeFromAllBlackList(HostSpec hostSpec) {
        removeFromBlackList(hostSpec);
        removeFromPreBlackList(hostSpec);
    }

    private void addToPreAbsentAndBlackPresent(HostSpec hostSpec) {
        addToPreAbsent(hostSpec);
        addToBlackListPresent(hostSpec);
    }

}
