package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfigValidator;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigXMLParser;
import com.ctrip.platform.dal.dao.util.FileReader;
import org.junit.Assert;
import org.junit.Test;

public class ClusterConfigValidatorTest {

    @Test
    public void testShardStrategiesValidation() {
        ClusterConfigValidator validator = new ClusterConfigXMLParser();
        String clusterName = "test";
        String config1 = FileReader.read("shard-strategies-valid");
        validator.validateShardStrategies(clusterName, config1);
        String config2 = FileReader.read("shard-strategies-invalid-1");
        try {
            validator.validateShardStrategies(clusterName, config2);
            Assert.fail("shard-strategies-invalid-1 failed");
        } catch (Exception e) {
            // expected
            e.printStackTrace();
        }
        String config3 = FileReader.read("shard-strategies-invalid-2");
        try {
            validator.validateShardStrategies(clusterName, config3);
            Assert.fail("shard-strategies-invalid-2 failed");
        } catch (Exception e) {
            // expected
            e.printStackTrace();
        }
        String config4 = FileReader.read("shard-strategies-invalid-3");
        try {
            validator.validateShardStrategies(clusterName, config3);
            Assert.fail("shard-strategies-invalid-3 failed");
        } catch (Exception e) {
            // expected
            e.printStackTrace();
        }
    }

    @Test
    public void testIdGeneratorsValidation() {
        ClusterConfigValidator validator = new ClusterConfigXMLParser();
        String clusterName = "test";
        String config1 = FileReader.read("idgen-valid");
        validator.validateIdGenerators(clusterName, config1);
        String config2 = FileReader.read("idgen-invalid-1");
        try {
            validator.validateIdGenerators(clusterName, config2);
            Assert.fail("idgen-invalid-1 failed");
        } catch (Exception e) {
            // expected
            e.printStackTrace();
        }
        String config3 = FileReader.read("idgen-invalid-2");
        try {
            validator.validateIdGenerators(clusterName, config3);
            Assert.fail("idgen-invalid-2 failed");
        } catch (Exception e) {
            // expected
            e.printStackTrace();
        }
    }

}
