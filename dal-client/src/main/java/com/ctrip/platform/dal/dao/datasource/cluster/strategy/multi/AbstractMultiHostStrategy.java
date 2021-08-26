package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ConnectionFactoryAware;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostValidatorAware;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ctrip.platform.dal.cluster.multihost.ClusterRouteStrategyConfig.CLUSTER_NAME;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public abstract class AbstractMultiHostStrategy implements MultiHostStrategy, ConnectionFactoryAware {

    protected static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    protected static final String ROUTER_ORDER_HOSTS = "Router::cluster:%s";
    protected static final String ORDER_HOSTS = "orderHosts:%s";
    private static final String ROUTER_INITIALIZE = "Router::initialize";
    private static final String INITIALIZE_MSG = "configuredHosts:%s;strategyOptions:%s";

    protected String CAT_LOG_TYPE;
    protected Set<HostSpec> configuredHosts;
    protected CaseInsensitiveProperties strategyOptions;
    protected String cluster;
    protected List<HostSpec> orderHosts;

    protected HostValidator hostValidator;

    @Override
    public void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties) {
        this.configuredHosts = new HashSet<>(hostSpecs);
        this.strategyOptions = strategyProperties;
        this.cluster = strategyProperties.getString(CLUSTER_NAME, "unknown-cluster");
        CAT_LOG_TYPE = getCatLogType();
        buildOrderHosts();
        buildHostValidator();
        LOGGER.info(ROUTER_INITIALIZE + ":" + String.format(INITIALIZE_MSG, configuredHosts.toString(), strategyOptions.toString()));
        LOGGER.logEvent(CAT_LOG_TYPE, ROUTER_INITIALIZE, String.format(INITIALIZE_MSG, configuredHosts.toString(), strategyOptions.toString()));
    }

    protected String getCatLogType() {
        String clazzName = getClass().getSimpleName();
        return clazzName.replaceAll("Strategy", "");
    }

    protected void buildOrderHosts() {
        doBuildOrderHosts();
        LOGGER.info(String.format(ROUTER_ORDER_HOSTS, cluster) + ":" + String.format(ORDER_HOSTS, orderHosts.toString()));
        LOGGER.logEvent(CAT_LOG_TYPE, String.format(ROUTER_ORDER_HOSTS, cluster), String.format(ORDER_HOSTS, orderHosts.toString()));
    }

    protected void buildHostValidator() {
        if (this instanceof HostValidatorAware) {
            return;
        }
        long failOverTime = strategyOptions.getLong(FAIL_OVER_TIME_MS, 10000);
        long blackListTimeOut = strategyOptions.getLong(BLACK_LIST_TIMEOUT_MS, 10000);
        long fixedValidatePeriod = strategyOptions.getLong(VALIDATE_PERIOD_MS, 30000);
        this.hostValidator = newHostValidator(configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    @Override
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        hostValidator.setConnectionFactory(connectionFactory);
    }

    @Override
    public SQLException interceptException(SQLException sqlEx, HostConnection conn) {
        try {
            hostValidator.validate(conn);
        } catch (Exception e) {
            // nothing to do
        }
        return sqlEx;
    }

    @Override
    public void dispose() {
        hostValidator.destroy();
    }

    abstract protected void doBuildOrderHosts();

    abstract protected HostValidator newHostValidator(Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod);

}
