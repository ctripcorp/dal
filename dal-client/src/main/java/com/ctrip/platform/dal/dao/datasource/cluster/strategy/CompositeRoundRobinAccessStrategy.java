package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.RequestContext;
import com.ctrip.platform.dal.dao.datasource.cluster.ShardMeta;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.HostValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.SimpleHostValidator;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * getConnectionValidator use default NullConnectionValidator due to OceanBase is stateless and work at lease one node alive
 * @Author limingdong
 * @create 2021/8/18
 */
public class CompositeRoundRobinAccessStrategy extends AbstractMultiHostStrategy implements MultiHostStrategy {

    private LocalizedAccessStrategy delegate;

    @Override
    public void initialize(ShardMeta shardMeta, ConnectionFactory connFactory, CaseInsensitiveProperties strategyProperties) {
        ZoneDividedStrategyContext strategyGenerator = new ZoneDividedStrategyContext(shardMeta, connFactory, strategyProperties);
        delegate = (LocalizedAccessStrategy) strategyGenerator.accept(new LocalizedStrategyTransformer());
        // start validator to monitor shardMeta in all zone instead of monitoring every zone in RoundRobinAccessStrategy to reducing thread resources
        buildValidator();
    }

    @Override
    public HostConnection pickConnection(RequestContext request) throws SQLException {
        return delegate.pickConnection(request);
    }

    @Override
    protected HostValidator newHostValidator(ConnectionFactory factory, Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        return new SimpleHostValidator(factory, configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }

    @Override
    protected void doBuildOrderHosts() {
        throw new UnsupportedOperationException("MultiRoundRobinAccessStrategy not support");
    }

    @Override
    protected void isInit() {
        if (delegate == null || delegate.isEmpty()) {
            throw new DalRuntimeException("MultiRoundRobinAccessStrategy is not set delegate");
        }
        super.isInit();
    }
}
