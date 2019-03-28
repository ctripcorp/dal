package com.ctrip.framework.db.cluster.controller;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.db.cluster.domain.MongoCluster;
import com.ctrip.framework.db.cluster.domain.Node;
import com.ctrip.framework.db.cluster.domain.ResponseModel;
import com.ctrip.framework.db.cluster.enums.ClusterType;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by shenjie on 2019/3/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MongoClusterControllerTest {

    public static final String MONGO_CLUSTER_ADD_URL = "/mongo/cluster/add";
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
        MongoCluster mongoCluster = generateMongoCluster();

        MvcResult mvcResult = mockMvc.perform(post(MONGO_CLUSTER_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("env", "fat")
                .param("operator", "test")
                .content(JSON.toJSONString(mongoCluster)))
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        ResponseModel response = JSON.parseObject(result, ResponseModel.class);
        assert response.getStatus() == 200;
    }

    private MongoCluster generateMongoCluster() {
        Node node = Node.builder()
                .host("node.host")
                .port(11111)
                .build();

        MongoCluster mongoCluster = MongoCluster.builder()
                .clusterName("MongoClusterControllerTest")
                .clusterType(ClusterType.REPLICATION.name())
                .dbName("testDBName")
                .userId("testUserId")
                .password("testPassword")
                .nodes(Lists.newArrayList(node))
                .build();

        return mongoCluster;
    }

}