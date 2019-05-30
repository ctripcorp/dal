package com.ctrip.framework.db.cluster.controller;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.db.cluster.domain.*;
import com.ctrip.framework.db.cluster.enums.ClusterType;
import com.ctrip.framework.db.cluster.util.Util;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by shenjie on 2019/3/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MongoClusterControllerTest {

    private static final String MONGO_CLUSTER_ADD_URL = "/mongo/cluster/add";
    private static final String MONGO_CLUSTER_UPDATE_URL = "/mongo/cluster/update";
    private static final String MONGO_CLUSTER_GET_URL = "/mongo/cluster/info";

    private static final String MONGO_CLUSTER_NAME = "diuserprofile-diuserprofiledb";
    private static final String FAT_ENV = "fat";
    private static final String OPERATOR = "test";
    private static final String NEW_USER_ID = "newUserId";
    // 模拟MVC对象，通过MockMvcBuilders.webAppContextSetup(this.wac).build()初始化。
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

    @Before // 在测试开始前初始化工作
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void add() throws Exception {
        MongoCluster mongoCluster = generateMongoCluster(UUID.randomUUID().toString());

        MvcResult mvcResult = mockMvc.perform(post(MONGO_CLUSTER_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("env", "fat")
                .param("operator", "test")
                .content(JSON.toJSONString(mongoCluster)))
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        ResponseModel response = JSON.parseObject(result, ResponseModel.class);
        assert response != null;
        assert response.getStatus() == 200;
    }

    @Test
    public void update() throws Exception {
        // add cluster
        MongoCluster mongoCluster = generateMongoCluster(UUID.randomUUID().toString());
        String clusterName = mongoCluster.getClusterName();
        String userId = mongoCluster.getUserId();
        MvcResult mvcResult = mockMvc.perform(post(MONGO_CLUSTER_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("env", FAT_ENV)
                .param("operator", OPERATOR)
                .content(JSON.toJSONString(mongoCluster)))
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        ResponseModel response = JSON.parseObject(result, ResponseModel.class);
        assert response != null;
        assert response.getStatus() == 200;

        // get cluster
        MvcResult getMvcResult = mockMvc.perform(get(MONGO_CLUSTER_GET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("clustername", clusterName)
                .param("env", FAT_ENV))
                .andReturn();

        result = getMvcResult.getResponse().getContentAsString();
        response = Util.gson.fromJson(result, ResponseModel.class);
        assert response.getStatus() == 200;
        Object data = response.getResult();
        String dataContent = Util.gson.toJson(data);
        MongoClusterInfo mongoClusterInfo = Util.gson.fromJson(dataContent, MongoClusterInfo.class);
        assert mongoClusterInfo != null;
        assert clusterName.equalsIgnoreCase(mongoCluster.getClusterName());
        assert userId.equalsIgnoreCase(mongoClusterInfo.getUserId());
        assert mongoClusterInfo.getEnabled();
        int version = mongoClusterInfo.getVersion();
        String updateTime = mongoClusterInfo.getUpdateTime();

        TimeUnit.SECONDS.sleep(2);

        // update cluster
        mongoCluster.setUserId(NEW_USER_ID);
        mongoCluster.setEnabled(false);
        mvcResult = mockMvc.perform(post(MONGO_CLUSTER_UPDATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("env", FAT_ENV)
                .param("operator", OPERATOR)
                .content(JSON.toJSONString(mongoCluster)))
                .andReturn();
        result = mvcResult.getResponse().getContentAsString();
        response = JSON.parseObject(result, ResponseModel.class);
        assert response != null;
        assert response.getStatus() == 200;

        // get cluster
        getMvcResult = mockMvc.perform(get(MONGO_CLUSTER_GET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("clustername", clusterName)
                .param("env", FAT_ENV))
                .andReturn();

        result = getMvcResult.getResponse().getContentAsString();
        response = Util.gson.fromJson(result, ResponseModel.class);
        assert response.getStatus() == 200;
        data = response.getResult();
        dataContent = Util.gson.toJson(data);
        mongoClusterInfo = Util.gson.fromJson(dataContent, MongoClusterInfo.class);
        assert mongoClusterInfo != null;
        assert clusterName.equalsIgnoreCase(mongoCluster.getClusterName());
        assert NEW_USER_ID.equalsIgnoreCase(mongoClusterInfo.getUserId());
        assert !mongoClusterInfo.getEnabled();
        assert version == mongoClusterInfo.getVersion() - 1;
        assert !updateTime.equalsIgnoreCase(mongoClusterInfo.getUpdateTime());
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
                .build();

        return mongoCluster;
    }

    @Test
    public void test() {
        PluginResponse response = new PluginResponse();
        response.setStatus(200);
        response.setData(generateMongoCluster("test"));
        MongoCluster mongoCluster = (MongoCluster) response.getData();
        System.out.println("test:" + mongoCluster);

    }

}