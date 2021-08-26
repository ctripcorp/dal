package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.CustomConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.DefaultRequestContext;
import com.ctrip.platform.dal.dao.datasource.cluster.RequestContext;
import com.ctrip.platform.dal.dao.datasource.cluster.ShardMeta;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.CompositeStrategyTransformer;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.StrategyTransformer;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.SimpleHostValidator;
import com.ctrip.platform.dal.exceptions.InvalidConnectionException;
import org.junit.Before;

import java.sql.Connection;
import java.util.*;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public abstract class ShardMetaGenerator {

    protected static final int SHARD_INDEX = new Random().nextInt(1000);

    protected static final String CLUSTER_NAME = "test_cluster_name_" + SHARD_INDEX;

    protected static final String SHAOY = "SHAOY";

    protected static final String SHAOY_IP1 = "127.0.0.2";
    protected static final String SHAOY_IP2 = "127.0.0.3";

    protected static final int SHAOY_PORT1 = 12345;
    protected static final int SHAOY_PORT2 = 12346;

    protected static final String SHARB = "SHARB";

    protected static final String SHARB_IP1 = "127.0.0.4";
    protected static final String SHARB_IP2 = "127.0.0.5";

    protected static final int SHARB_PORT1 = 12347;
    protected static final int SHARB_PORT2 = 12348;

    protected static final String SHAXY = "SHAXY";

    protected static final String SHAXY_IP1 = "127.0.0.6";
    protected static final String SHAXY_IP2 = "127.0.0.7";

    protected static final int SHAXY_PORT1 = 12349;
    protected static final int SHAXY_PORT2 = 12350;

    protected Set<HostSpec> hostSpecs = new HashSet<>();

    protected HostSpec HostSpecOY_1 = new HostSpec(SHAOY_IP1, SHAOY_PORT1, SHAOY, true);
    protected HostSpec HostSpecOY_2 = new HostSpec(SHAOY_IP2, SHAOY_PORT2, SHAOY, true);

    protected ShardMeta shardMeta;

    protected DalHints dalHints = new DalHints();

    protected CaseInsensitiveProperties caseInsensitiveProperties = new CaseInsensitiveProperties();

    protected RouteStrategy routeStrategy;

    protected ConnectionFactory connectionFactory = new ConnectionFactory() {
        @Override
        public Connection getPooledConnectionForHost(HostSpec host) throws InvalidConnectionException {
            return new CustomConnection();
        }
    };

    protected HostValidator newHostValidator(Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        return new SimpleHostValidator(configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    protected RequestContext requestContext;

    protected HostValidator hostValidator;

    protected StrategyTransformer strategyTransformer = new CompositeStrategyTransformer();

    @Before
    public void setUp() throws Exception {
        shardMeta = new TestShardMeta();
        requestContext = new DefaultRequestContext(getRequestZone());

        addOY();
        addRB();
        addXY();

        hostValidator = newHostValidator(hostSpecs, new ArrayList<>(hostSpecs), 10000, 10000, 30000);

        routeStrategy = getRouteStrategy();
        if (routeStrategy != null) {
            try {
                routeStrategy.init(shardMeta.configuredHosts(), caseInsensitiveProperties);
            } catch (UnsupportedOperationException e) {
                // nothing to do
            }
            if (routeStrategy.isWrapperFor(ConnectionFactoryAware.class)) {
                routeStrategy.unwrap(ConnectionFactoryAware.class).setConnectionFactory(connectionFactory);
            }
        }
    }

    abstract protected RouteStrategy getRouteStrategy();

    protected String getRequestZone() {
        return SHAOY;
    }

    protected void addOY() {
        hostSpecs.add(HostSpecOY_1);
        hostSpecs.add(HostSpecOY_2);
    }

    protected void addRB() {
        hostSpecs.add(new HostSpec(SHARB_IP1, SHARB_PORT1, SHARB, true));
        hostSpecs.add(new HostSpec(SHARB_IP2, SHARB_PORT2, SHARB, true));
    }

    protected void addXY() {
        hostSpecs.add(new HostSpec(SHAXY_IP1, SHAXY_PORT1, SHAXY, true));
        hostSpecs.add(new HostSpec(SHAXY_IP2, SHAXY_PORT2, SHAXY, true));
    }

    class TestShardMeta implements ShardMeta {

        @Override
        public int shardIndex() {
            return SHARD_INDEX;
        }

        @Override
        public Set<HostSpec> configuredHosts() {
            return hostSpecs;
        }

        @Override
        public String clusterName() {
            return CLUSTER_NAME;
        }
    }
}
