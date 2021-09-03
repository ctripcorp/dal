package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGenerator;
import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGeneratorConfig;
import org.w3c.dom.Node;

import java.io.InputStream;

public class MockIdGeneratorConfigXMLParser implements IdGeneratorConfigXMLParser {

    @Override
    public ClusterIdGeneratorConfig parse(String clusterName, Node xmlNode) {
        return new MockIdGeneratorConfig(clusterName);
    }

    @Override
    public ClusterIdGeneratorConfig parse(String clusterName, String content) {
        return new MockIdGeneratorConfig(clusterName);
    }

    @Override
    public ClusterIdGeneratorConfig parse(String clusterName, InputStream stream) {
        return new MockIdGeneratorConfig(clusterName);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    private static class MockIdGeneratorConfig implements ClusterIdGeneratorConfig {
        private final String clusterName;

        MockIdGeneratorConfig(String clusterName) {
            this.clusterName = clusterName;
        }

        @Override
        public ClusterIdGenerator getIdGenerator(String name) {
            return new MockIdGenerator(name);
        }

        @Override
        public String getSequenceDbName() {
            return clusterName;
        }

        @Override
        public int warmUp() {
            return 0;
        }
    }

    private static class MockIdGenerator implements ClusterIdGenerator {
        private final String sequenceName;

        MockIdGenerator(String sequenceName) {
            this.sequenceName = sequenceName;
        }

        @Override
        public Number nextId() {
            return (sequenceName + System.currentTimeMillis()).hashCode() + System.currentTimeMillis();
        }
    }

}
