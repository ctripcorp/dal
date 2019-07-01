package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.domain.*;
import com.ctrip.framework.db.cluster.enums.ClusterType;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by shenjie on 2019/4/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PluginMongoServiceTest {

    private static final String MONGO_CLUSTER_NAME = "diuserprofile-diuserprofiledb";
    private static final String FAT_ENV = "fat";
    private static final String LPT_ENV = "lpt";
    private static final String OPERATOR = "test";
    private static final String NEW_USER_ID = "newUserId";

    @Autowired
    private PluginMongoService pluginMongoService;

    @Test
    public void add() throws Exception {
        MongoCluster mongoCluster = generateMongoCluster(UUID.randomUUID().toString());
        PluginResponse response = pluginMongoService.add(mongoCluster, FAT_ENV, LPT_ENV, OPERATOR);
        assert response != null;
        assert response.getStatus() == 0;
    }

    @Test
    public void update() throws Exception {
        // add cluster
        MongoCluster mongoCluster = generateMongoCluster(UUID.randomUUID().toString());
        String clusterName = mongoCluster.getClusterName();
        String userId = mongoCluster.getUserId();
        PluginResponse response = pluginMongoService.add(mongoCluster, FAT_ENV, LPT_ENV, OPERATOR);
        assert response != null;
        assert response.getStatus() == 0;

        // get cluster
        MongoClusterGetResponse getResponse = pluginMongoService.get(clusterName, FAT_ENV, LPT_ENV);
        assert getResponse != null;
        assert getResponse.getStatus() == 0;
        MongoClusterInfo data = getResponse.getData();
        assert data != null;
        assert clusterName.equalsIgnoreCase(data.getClusterName());
        assert userId.equalsIgnoreCase(data.getUserId());
        assert data.getEnabled();
        int version = data.getVersion();
        String updateTime = data.getUpdateTime();

        TimeUnit.SECONDS.sleep(2);

        // update cluster
        mongoCluster.setEnabled(false);
        mongoCluster.setUserId(NEW_USER_ID);
        PluginResponse updateResponse = pluginMongoService.update(mongoCluster, FAT_ENV, LPT_ENV, OPERATOR);
        assert updateResponse != null;
        assert updateResponse.getStatus() == 0;

        // get cluster
        getResponse = pluginMongoService.get(clusterName, FAT_ENV, LPT_ENV);
        assert getResponse != null;
        assert getResponse.getStatus() == 0;
        data = getResponse.getData();
        assert data != null;
        assert clusterName.equalsIgnoreCase(data.getClusterName());
        assert NEW_USER_ID.equalsIgnoreCase(data.getUserId());
        assert !data.getEnabled();
        assert version == data.getVersion() - 1;
        assert !updateTime.equalsIgnoreCase(data.getUpdateTime());
    }

    @Test
    public void get() throws Exception {
        MongoClusterGetResponse response = pluginMongoService.get("7e9ddf6b-4855-4a16-b194-7ec81ca89eef-test", FAT_ENV, LPT_ENV);
        assert response != null;
        assert response.getStatus() == 0;
        assert (response.getData() instanceof MongoClusterInfo);
    }

    private MongoCluster generateMongoCluster(String clusterName) {
        if (!MONGO_CLUSTER_NAME.equalsIgnoreCase(clusterName)) {
            clusterName = clusterName + "-test";
        }

        Node node = Node.builder()
                .host("bridge.soa.uat.qa.nt.ctripcorp.com")
                .port(65535)
                .build();

        MongoCluster mongoCluster = MongoCluster.builder()
                .clusterName(clusterName)
                .clusterType(ClusterType.REPLICATION.name())
                .dbName("testDBtestDBtestDBtestDB")
                .userId("testName")
                .password("qwe123")
                .nodes(Lists.newArrayList(node))
                .enabled(true)
                .version(1)
                .build();

        return mongoCluster;
    }

}