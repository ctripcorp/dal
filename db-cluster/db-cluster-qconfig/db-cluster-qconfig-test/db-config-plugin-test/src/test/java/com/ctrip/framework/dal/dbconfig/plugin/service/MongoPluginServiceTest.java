package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.MongoClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.Node;
import com.ctrip.framework.dal.dbconfig.plugin.entity.PluginResponse;
import com.ctrip.framework.dal.dbconfig.plugin.entity.SslCodeGetResponse;
import com.ctrip.framework.dal.dbconfig.plugin.util.ConfigUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.Utils;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

/**
 * Created by shenjie on 2019/4/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MongoPluginServiceTest {

    public static final String FAT_ENV = "fat";
    public static final String UAT_ENV = "uat";
    public static final String PRO_ENV = "pro";
    public static final String OPERATOR = "mongoTest";
    public static final String FWS_SSL_CODE = "VZ00000000000441";
    public static final String UPDATE_SSL_CODE = "HN00000000002356";
    public static final String BAD_SSL_CODE = "HN111111111112356";
    public static final String MONGO_CLUSTER_NAME = "diuserprofile-diuserprofiledb";

    @Autowired
    private MongoPluginService mongoPluginService;
    @Autowired
    private TitanPluginService titanPluginService;

    @Test
    public void addExistMongoCluster() throws Exception {
        MongoClusterEntity mongoCluster = generateMongoClusterEntity(MONGO_CLUSTER_NAME);
        PluginResponse response = mongoPluginService.addMongoCluster(mongoCluster, FAT_ENV, OPERATOR);
        assert response.getStatus() != 0;

        // get client config from fat16, need add vm option.
        System.setProperty("qconfig.admin", "qconfig.fat16.qa.nt.ctripcorp.com");
        System.setProperty("qserver.http.urls", "10.5.80.175:8080");
        System.setProperty("qserver.https.urls", "10.5.80.175:8443");
        String content = ConfigUtils.getMongoFileResult(mongoCluster.getClusterName());
        assert Strings.isNotBlank(content);
    }

    @Test
    public void addAbsentMongoCluster() throws Exception {
        MongoClusterEntity mongoCluster = generateMongoClusterEntity(UUID.randomUUID().toString());
        PluginResponse response = mongoPluginService.addMongoCluster(mongoCluster, FAT_ENV, OPERATOR);
        assert response.getStatus() == 0;

        // get client config from fat16, need add vm option.
        System.setProperty("qconfig.admin", "qconfig.fat16.qa.nt.ctripcorp.com");
        System.setProperty("qserver.http.urls", "10.5.80.175:8080");
        System.setProperty("qserver.https.urls", "10.5.80.175:8443");
        String content = ConfigUtils.getMongoFileResult(mongoCluster.getClusterName());
        assert Strings.isNotBlank(content);
    }

    @Test
    public void test() throws Exception {
        // add cluster
        MongoClusterEntity mongoCluster1 = generateMongoClusterEntity(UUID.randomUUID().toString());
        PluginResponse addClusterResponse1 = mongoPluginService.addMongoCluster(mongoCluster1, FAT_ENV, OPERATOR);
        assert addClusterResponse1.getStatus() == 0;

        // get client config from fat16, need add vm option.
        String clientResult1 = ConfigUtils.getMongoFileResult(mongoCluster1.getClusterName());
        MongoClusterEntity entity1 = Utils.gson.fromJson(clientResult1, MongoClusterEntity.class);
        assert entity1.getUserId().equalsIgnoreCase(mongoCluster1.getUserId());
        assert entity1.getPassword().equalsIgnoreCase(mongoCluster1.getPassword());

        // update sslcode
        PluginResponse updateSslCodeResponse = titanPluginService.updateSslCode(UPDATE_SSL_CODE, FAT_ENV, OPERATOR);
        assert updateSslCodeResponse.getStatus() == 0;

        // get sslCode
        SslCodeGetResponse sslCodeGetResponse = titanPluginService.getSslCode(FAT_ENV);
        assert sslCodeGetResponse.getStatus() == 0;
        assert UPDATE_SSL_CODE.equalsIgnoreCase(sslCodeGetResponse.getData());

        // add cluster
        MongoClusterEntity mongoCluster2 = generateMongoClusterEntity(UUID.randomUUID().toString());
        PluginResponse addClusterResponse2 = mongoPluginService.addMongoCluster(mongoCluster2, FAT_ENV, OPERATOR);
        assert addClusterResponse2.getStatus() == 0;

        // get client config
        String clientResult2 = ConfigUtils.getMongoFileResult(mongoCluster2.getClusterName());
        MongoClusterEntity entity2 = Utils.gson.fromJson(clientResult2, MongoClusterEntity.class);
        assert entity2.getUserId().equalsIgnoreCase(mongoCluster2.getUserId());
        assert entity2.getPassword().equalsIgnoreCase(mongoCluster2.getPassword());

        // get client config
        MongoClusterEntity mongoCluster3 = generateMongoClusterEntity(MONGO_CLUSTER_NAME);
        String clientResult3 = ConfigUtils.getMongoFileResult(mongoCluster3.getClusterName());
        MongoClusterEntity entity3 = Utils.gson.fromJson(clientResult3, MongoClusterEntity.class);
        assert entity3.getUserId().equalsIgnoreCase(mongoCluster3.getUserId());
        assert entity3.getPassword().equalsIgnoreCase(mongoCluster3.getPassword());

    }

    private MongoClusterEntity generateMongoClusterEntity(String clusterName) {

        if (!MONGO_CLUSTER_NAME.equalsIgnoreCase(clusterName)) {
            clusterName = clusterName + "_mongtest";
        }

        Node node = Node.builder()
                .host("bridge.soa.uat.qa.nt.ctripcorp.com")
                .port(65535)
                .build();

        MongoClusterEntity mongoCluster = MongoClusterEntity.builder()
                .clusterName(clusterName)
                .clusterType("REPLICATION")
                .dbName("testDBName")
                .userId("testName")
                .password("qwe123")
                .nodes(Lists.newArrayList(node))
                .version(1)
                .build();

        return mongoCluster;
    }

}