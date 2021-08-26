package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.cluster.ClusterType;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.*;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.ConnectionValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.HostValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.HostValidatorAware;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.MajorityHostValidator;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.exceptions.InvalidConnectionException;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public abstract class AbstractMultiHostStrategy implements MultiHostStrategy {

    protected static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String ROUTER_INITIALIZE = "Router::initialize";
    private static final String INITIALIZE_MSG = "configuredHosts:%s;strategyOptions:%s";
    protected static final String VALIDATE_FAILED = "Router::validateFailed";
    protected static final String CURRENT_HOST = "Router::currentHost:";
    protected static final String ROUTER_ORDER_HOSTS = "Router::cluster:%s";
    public static final String NO_HOST_AVAILABLE = "Router::noHostAvailable:%s";
    protected static final String ORDER_HOSTS = "orderHosts:%s";
    protected static final String CONNECTION_HOST_CHANGE = "Router::connectionHostChange:%s";
    protected static final String CHANGE_FROM_TO = "change from %s to %s";
    protected static final String CREATE_CONNECTION_FAILED = "Router::createConnectionFailed";

    protected String CAT_LOG_TYPE;
    protected HostValidator hostValidator;
    protected Set<HostSpec> configuredHosts;
    protected ConnectionFactory connFactory;
    protected CaseInsensitiveProperties strategyOptions;
    protected List<HostSpec> orderHosts;
    protected String cluster = "";
    private volatile String status; // birth --> init --> destroy

    private enum RouteStrategyStatus {
        birth, init, destroy
    }

    protected AbstractMultiHostStrategy() {
        status = RouteStrategyStatus.birth.name();
    }

    @Override
    public HostConnection pickConnection(RequestContext context) throws SQLException {
        isInit();
        return doPickConnection();
    }

    protected HostConnection doPickConnection() throws SQLException {
        for (int i = 0; i < configuredHosts.size(); i++) {
            HostSpec targetHost = null;
            try {
                targetHost = pickHost();
                return tryPickConnection(targetHost);
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
                if (!networkFailFast()) {
                    throw e;
                }
            }
        }

        LOGGER.logEvent(CAT_LOG_TYPE, String.format(NO_HOST_AVAILABLE, ""), orderHosts.toString());
        throw new DalException(NO_HOST_AVAILABLE);
    }

    protected HostConnection tryPickConnection(HostSpec targetHost) throws SQLException {
        Connection targetConnection = connFactory.getPooledConnectionForHost(targetHost);
        LOGGER.logEvent(CAT_LOG_TYPE, CURRENT_HOST + targetHost.toString(), cluster);
        return new DefaultHostConnection(targetConnection, targetHost);
    }

    protected boolean networkFailFast() {
        return false;
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

        CAT_LOG_TYPE = getCatLogType();
        LOGGER.info(ROUTER_INITIALIZE + ":" + String.format(INITIALIZE_MSG, configuredHosts.toString(), strategyOptions.toString()));
        LOGGER.logEvent(CAT_LOG_TYPE, ROUTER_INITIALIZE, String.format(INITIALIZE_MSG, configuredHosts.toString(), strategyOptions.toString()));
    }

    protected void buildValidator() {
        if (this instanceof HostValidatorAware) {
            return;
        }
        long failOverTime = strategyOptions.getLong("failoverTimeMS", 10000);
        long blackListTimeOut = strategyOptions.getLong("blacklistTimeoutMS", 10000);
        long fixedValidatePeriod = strategyOptions.getLong("fixedValidatePeriodMS", 30000);
        this.hostValidator = newHostValidator(connFactory, configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    protected HostValidator newHostValidator(ConnectionFactory factory, Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        return new MajorityHostValidator(factory, configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    protected void isInit() {
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
        return hostValidator;
    }

    @Override
    public void destroy() {
        isInit();
        hostValidator.destroy();
        this.status = RouteStrategyStatus.destroy.name();
    }

    protected HostSpec pickHost() throws DalException {
        for (HostSpec hostSpec : orderHosts) {
            if (hostValidator.available(hostSpec)) {
                return hostSpec;
            }
        }

        throw new DalException(String.format(NO_HOST_AVAILABLE, orderHosts.toString()));
    }

    protected void buildOrderHosts() {
        doBuildOrderHosts();
        LOGGER.info(String.format(ROUTER_ORDER_HOSTS, cluster) + ":" + String.format(ORDER_HOSTS, orderHosts.toString()));
        LOGGER.logEvent(CAT_LOG_TYPE, String.format(ROUTER_ORDER_HOSTS, cluster), String.format(ORDER_HOSTS, orderHosts.toString()));
    }

    protected String getCatLogType() {
        return "DAL." + ClusterType.MGR.getValue();
    }

    abstract protected void doBuildOrderHosts();
}
