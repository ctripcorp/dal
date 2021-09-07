package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
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

import static com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig.CLUSTER_NAME;
import static com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig.DEFAULT_CLUSTER_NAME_VALUE;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public abstract class AbstractMultiMasterStrategy implements MultiMasterStrategy, ConnectionFactoryAware {

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
        initProperties(strategyProperties);
        this.cluster = strategyProperties.getString(CLUSTER_NAME, DEFAULT_CLUSTER_NAME_VALUE);
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
        long failOverTime = strategyOptions.getLong(FAILOVER_TIME_MS, DEFAULT_FAILOVER_TIME_MS_VALUE);
        long blackListTimeOut = strategyOptions.getLong(BLACKLIST_TIMEOUT_MS, DEFAULT_BLACKLIST_TIMEOUT_MS_VALUE);
        long fixedValidatePeriod = strategyOptions.getLong(FIXED_VALIDATE_PERIOD_MS, DEFAULT_FIXED_VALIDATE_PERIOD_MS_VALUE);
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

    protected void initProperties(CaseInsensitiveProperties strategyProperties) {
        putIfAbsent(strategyProperties, FAILOVER_TIME_MS, String.valueOf(DEFAULT_FAILOVER_TIME_MS_VALUE));
        putIfAbsent(strategyProperties, BLACKLIST_TIMEOUT_MS, String.valueOf(DEFAULT_BLACKLIST_TIMEOUT_MS_VALUE));
        putIfAbsent(strategyProperties, FIXED_VALIDATE_PERIOD_MS, String.valueOf(DEFAULT_FIXED_VALIDATE_PERIOD_MS_VALUE));
        putIfAbsent(strategyProperties, ZONES_PRIORITY, String.valueOf(DEFAULT_ZONES_PRIORITY_VALUE));
        putIfAbsent(strategyProperties, MULTI_MASTER, String.valueOf(DEFAULT_MULTI_MASTER_VALUE));
        putIfAbsent(strategyProperties, CLUSTER_NAME, DEFAULT_CLUSTER_NAME_VALUE);
    }

    protected void putIfAbsent(CaseInsensitiveProperties strategyProperties, String key, String value) {
        if (strategyProperties.get(key) == null) {
            strategyProperties.set(key, value);
        }
    }

    abstract protected void doBuildOrderHosts();

    abstract protected HostValidator newHostValidator(Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod);

}
