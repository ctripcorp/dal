package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.client.CustomConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.DefaultRequestContext;
import com.ctrip.platform.dal.dao.datasource.cluster.RequestContext;
import com.ctrip.platform.dal.dao.datasource.cluster.ShardMeta;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.HostValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.SimpleHostValidator;
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

    protected ShardMeta shardMeta;

    protected CaseInsensitiveProperties caseInsensitiveProperties = new CaseInsensitiveProperties();

    protected ConnectionFactory connectionFactory = new ConnectionFactory() {
        @Override
        public Connection getPooledConnectionForHost(HostSpec host) throws InvalidConnectionException {
            return new CustomConnection();
        }

        @Override
        public Connection createConnectionForHost(HostSpec host) throws InvalidConnectionException {
            return new CustomConnection();
        }
    };

    protected HostValidator newHostValidator(ConnectionFactory factory, Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        return new SimpleHostValidator(factory, configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    protected RequestContext requestContext;

    protected HostValidator hostValidator;

    protected StrategyTransformer strategyTransformer = new LocalizedStrategyTransformer();

    @Before
    public void setUp() throws Exception {
        shardMeta = new TestShardMeta();
        requestContext = new DefaultRequestContext(getRequestZone());

        addOY();
        addRB();
        addXY();

        hostValidator = newHostValidator(connectionFactory, hostSpecs, new ArrayList<>(hostSpecs), 10000, 10000, 30000);
    }

    protected String getRequestZone() {
        return SHAOY;
    }

    protected void addOY() {
        hostSpecs.add(new HostSpec(SHAOY_IP1, SHAOY_PORT1, SHAOY, true));
        hostSpecs.add(new HostSpec(SHAOY_IP2, SHAOY_PORT2, SHAOY, true));
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
