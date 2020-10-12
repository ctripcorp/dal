package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultRouteStrategy implements RouteStrategy{

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CAT_LOG_TYPE = "DAL.pickConnection";
    private static final String HOST_NOT_EXIST = "Router::hostNotExist:%s";
    private static final String NO_HOST_AVAILABLE = "Router::hostBlackList:%s";

    @Override
    public Connection pickConnection(ConnectionFactory factory, RequestContext context, RouteOptions options) throws SQLException {

        String clientZone = context.clientZone();
        List<HostSpec> orderHosts = options.orderedMasters(clientZone);
        Set<HostSpec> configuredHosts = options.configuredHosts();
        Long blackTimeOut = options.blacklistTimeout();

        HostSpec targetHost = pickHost(orderHosts, configuredHosts, blackTimeOut);
        Connection targetConnection = factory.getConnectionForHost(targetHost);
        return targetConnection;
    }

    private HostSpec pickHost(List<HostSpec> orderHosts, Set<HostSpec> configuredHosts, long blackTimeOut) throws DalException {
        Map<HostSpec, Long> hostBlackList = GlobalBlackListManager.getHostBlackList();
        for (HostSpec hostSpec : orderHosts) {
            if (!configuredHosts.contains(hostSpec)) {
                LOGGER.warn(String.format(HOST_NOT_EXIST, hostSpec.toString()));
                continue;
            }
            if (hostBlackList.containsKey(hostSpec) && hostBlackList.get(hostSpec) + blackTimeOut> System.currentTimeMillis()){
                continue;
            }
            return hostSpec;
        }

        for (HostSpec hostSpec : configuredHosts) {
            if (!orderHosts.contains(hostSpec)) {
                return hostSpec;
            }
        }
        LOGGER.logEvent(CAT_LOG_TYPE, NO_HOST_AVAILABLE, hostBlackList.keySet().toString());
        throw new DalException(String.format(NO_HOST_AVAILABLE, hostBlackList.keySet().toString()));
    }
}
