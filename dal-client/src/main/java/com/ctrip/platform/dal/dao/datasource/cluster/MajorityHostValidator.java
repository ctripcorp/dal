package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.HostAndPort;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class MajorityHostValidator implements ConnectionValidator {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CAT_LOG_TYPE = "DAL.pickConnection";
    private static final String FIND_NO_HOST_SPEC = "Validator::findNoHostSpec";
    private static final String CONNECTION_URL = "Validator::getConnectionUrl";
    private static final String BLACK_LIST = "Validator::addTo%sBlackHost";
    private static final String DEFAULT = "default";

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

    @Override
    public Result validate(Connection connection, RouteOptions options) {
        Set<HostSpec> configuredHost = options.configuredHosts();
        HostSpec currentHost = getHostSpec(connection, configuredHost);
        try {
            int totalCount = options.configuredHosts().size();
            boolean currentHostState = false;
            int onlineCount = 0;

            ResultSet resultSet = connection.createStatement().executeQuery(validateSQL1);
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

            if (currentHostState && 2 * onlineCount > totalCount) {
                return Result.OK;
            }

            addToBlackList(currentHost);
            return Result.FAILED;
        } catch (SQLException e) {
            addToPreBlackList(currentHost);
            return Result.UNKNOWN;
        }
    }

    private HostSpec getHostSpec(Connection connection, Set<HostSpec> configuredHost) {
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

    private void addToPreBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            LOGGER.warn(String.format(BLACK_LIST, "Pre"));
            return;
        }
        GlobalBlackListDepository depository = GlobalBlackListManager.getBlackListDepository();
        depository.addHostToBlackList(hostSpec);
        // 异步定时去validate，根据定制的周期和频率
    }

    private void addToBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            LOGGER.warn(String.format(BLACK_LIST, ""));
            return;
        }
        GlobalBlackListDepository depository = GlobalBlackListManager.getBlackListDepository();
        depository.addToPreBlackList(hostSpec);
    }

}
