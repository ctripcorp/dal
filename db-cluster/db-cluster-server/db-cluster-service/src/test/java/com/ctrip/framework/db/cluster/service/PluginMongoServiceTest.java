package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.domain.MongoCluster;
import com.ctrip.framework.db.cluster.domain.Node;
import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.enums.ClusterType;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by shenjie on 2019/4/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PluginMongoServiceTest {

    @Autowired
    private PluginMongoService pluginMongoService;

    @Test
    public void add() throws Exception {
        MongoCluster mongoCluster = generateMongoCluster();
        PluginResponse response = pluginMongoService.add(mongoCluster, "fat", "test");
        assert response.getStatus() == 0;
    }

    private MongoCluster generateMongoCluster() {
        Node node = Node.builder()
                .host("bridge.soa.uat.qa.nt.ctripcorp.com")
                .port(65535)
                .build();

        MongoCluster mongoCluster = MongoCluster.builder()
                .clusterName("diuserprofile-diuserprofiledb")
                .clusterType(ClusterType.REPLICATION.name())
                .dbName("testDBtestDBtestDBtestDB")
                .userId("testName")
                .password("qwe123")
                .nodes(Lists.newArrayList(node))
                .build();

        return mongoCluster;
    }

}