package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.exceptions.InvalidConnectionException;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class OrderedAccessStrategy implements RouteStrategy{

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CAT_LOG_TYPE = "MGR.pickConnection";
    private static final String VALIDATE_FAILED = "Router::validateFailed";
    private static final String CURRENT_HOST = "Router::currentHost:";
    private static final String ROUTER_INITIALIZE = "Router::initialize";
    private static final String ROUTER_ORDER_HOSTS = "Router::cluster:%s";
    private static final String NO_HOST_AVAILABLE = "Router::noHostAvailable:%s";
    private static final String INITIALIZE_MSG = "configuredHosts:%s;strategyOptions:%s";
    private static final String ORDER_HOSTS = "orderHosts:%s";
    private static final String CONNECTION_HOST_CHANGE = "Router::connectionHostChange:%s";
    private static final String CHANGE_FROM_TO = "change from %s to %s";
    private static final String CREATE_CONNECTION_FAILED = "Router::createConnectionFailed";

    private ConnectionValidator connectionValidator;
    private HostValidator hostValidator;
    private Set<HostSpec> configuredHosts;
    private ConnectionFactory connFactory;
    private CaseInsensitiveProperties strategyOptions;
    private List<HostSpec> orderHosts;
    private volatile HostSpec currentHost;
    private String cluster = "";
    private String status; // birth --> init --> destroy

    private enum RouteStrategyStatus {
        birth, init, destroy
    }

    public OrderedAccessStrategy() {
        status = RouteStrategyStatus.birth.name();
    }

    @Override
    public HostConnection pickConnection(RequestContext context) throws SQLException {
        isInit();
        for (int i = 0; i < configuredHosts.size(); i++) {
            HostSpec targetHost = null;
            try {
                targetHost = pickHost();
                synchronized (this) {
                    if (!targetHost.equals(currentHost)) {
                        LOGGER.warn(String.format(CONNECTION_HOST_CHANGE, String.format(CHANGE_FROM_TO, currentHost.toString(), targetHost.toString())));
                        LOGGER.logEvent(CAT_LOG_TYPE, String.format(CONNECTION_HOST_CHANGE, cluster), String.format(CHANGE_FROM_TO, currentHost.toString(), targetHost.toString()));
                        currentHost = targetHost;
                    }
                }
                Connection targetConnection = connFactory.getPooledConnectionForHost(targetHost);
                LOGGER.logEvent(CAT_LOG_TYPE, CURRENT_HOST + targetHost.toString(), cluster);
                return new DefaultHostConnection(targetConnection, targetHost);
            } catch (InvalidConnectionException e) {
                if (targetHost != null){
                    LOGGER.warn(VALIDATE_FAILED + targetHost.toString());
                    LOGGER.logEvent(CAT_LOG_TYPE, VALIDATE_FAILED, targetHost.toString());
                }
                hostValidator.triggerValidate();
            } catch (CommunicationsException e) {
                LOGGER.warn(CREATE_CONNECTION_FAILED + targetHost.toString());
                LOGGER.logEvent(CAT_LOG_TYPE, CREATE_CONNECTION_FAILED, targetHost.toString());
                hostValidator.triggerValidate();
                throw e;
            }
        }

        LOGGER.logEvent(CAT_LOG_TYPE, String.format(NO_HOST_AVAILABLE, ""), orderHosts.toString());
        throw new DalException(NO_HOST_AVAILABLE);
    }

    @Override
    public void initialize(ShardMeta shardMeta, ConnectionFactory connFactory, CaseInsensitiveProperties strategyProperties) {
        isDestroy();
        this.status = RouteStrategyStatus.init.name();
        this.configuredHosts = shardMeta.configuredHosts();
        this.cluster = shardMeta.clusterName();
        this.connFactory = connFactory;
        this.strategyOptions = strategyProperties;
        buildOrderHosts();
        buildValidator();
        this.currentHost = orderHosts.get(0);
        LOGGER.info(ROUTER_INITIALIZE + ":" + String.format(INITIALIZE_MSG, configuredHosts.toString(), strategyOptions.toString()));
        LOGGER.logEvent(CAT_LOG_TYPE, ROUTER_INITIALIZE, String.format(INITIALIZE_MSG, configuredHosts.toString(), strategyOptions.toString()));
    }

    private void buildOrderHosts () {
        List<String> zoneOrder = strategyOptions.getStringList("zonesPriority", ",", null);
        ZonedHostSorter sorter = new ZonedHostSorter(zoneOrder);
        this.orderHosts = sorter.sort(configuredHosts);
        LOGGER.info(String.format(ROUTER_ORDER_HOSTS, cluster) + ":" + String.format(ORDER_HOSTS, orderHosts.toString()));
        LOGGER.logEvent(CAT_LOG_TYPE, String.format(ROUTER_ORDER_HOSTS, cluster), String.format(ORDER_HOSTS, orderHosts.toString()));
    }

    private void buildValidator() {
        long failOverTime = strategyOptions.getLong("failoverTimeMS", 10000);
        long blackListTimeOut = strategyOptions.getLong("blacklistTimeoutMS", 10000);
        long fixedValidatePeriod = strategyOptions.getLong("fixedValidatePeriodMS", 30000);
        MajorityHostValidator validator = new MajorityHostValidator(connFactory, configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
        this.connectionValidator = validator;
        this.hostValidator = validator;
    }

    private void isInit() {
        if (!RouteStrategyStatus.init.name().equalsIgnoreCase(status))
            throw new DalRuntimeException("OrderedAccessStrategy is not ready, status: " + this.status);
    }

    private void isDestroy () {
        if (RouteStrategyStatus.init.name().equalsIgnoreCase(status))
            throw new DalRuntimeException("OrderedAccessStrategy has been init, status: " + this.status);
    }

    @Override
    public ConnectionValidator getConnectionValidator(){
        isInit();
        return connectionValidator;
    }

    @Override
    public void destroy() {
        isInit();
        hostValidator.destroy();
        this.status = RouteStrategyStatus.destroy.name();
    }

    private HostSpec pickHost() throws DalException {
        for (HostSpec hostSpec : orderHosts) {
            if (hostValidator.available(hostSpec)) {
                return hostSpec;
            }
        }

        throw new DalException(String.format(NO_HOST_AVAILABLE, orderHosts.toString()));
    }
}
