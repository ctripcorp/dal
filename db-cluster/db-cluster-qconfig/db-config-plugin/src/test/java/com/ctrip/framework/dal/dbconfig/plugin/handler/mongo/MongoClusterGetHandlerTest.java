package com.ctrip.framework.dal.dbconfig.plugin.handler.mongo;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.MongoConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.mongo.MongoClusterGetOutputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qunar.tc.qconfig.plugin.PluginConstant;
import qunar.tc.qconfig.plugin.PluginResult;
import qunar.tc.qconfig.plugin.PluginStatusCode;
import qunar.tc.qconfig.plugin.QconfigService;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shenjie on 2019/5/29.
 */
public class MongoClusterGetHandlerTest implements MongoConstants {
    private String clusterName = "demoMongoCluster";
    private String env = "fat";
    private String subEnv = "fat12";
    private HttpServletRequest request;
    private AdminHandler handler;

    public MongoClusterGetHandlerTest() {
        QconfigService service = new MockQconfigService();
        PluginConfigManager pluginConfigManager = new PluginConfigManager(service);
        handler = new MongoClusterGetHandler(service, pluginConfigManager);
    }

    @Before
    public void beforeTest() {
        request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter(REQ_PARAM_ENV)).andReturn(env).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_SUB_ENV)).andReturn(subEnv).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_CLUSTER_NAME)).andReturn(clusterName).anyTimes();
    }

    @Test
    public void testPreHandle() {
        request.setAttribute(EasyMock.eq(REQ_ATTR_CLUSTER_NAME), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.replay(request);

        PluginResult result = handler.preHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);
    }

    @Test
    public void testPostHandle() {
        EnvProfile profile = new EnvProfile(env);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(profile).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_CLUSTER_NAME)).andReturn(clusterName).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();
        EasyMock.replay(request);

        PluginResult result = handler.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);

        MongoClusterGetOutputEntity mongoCluster = (MongoClusterGetOutputEntity) result.getAttribute();
        Assert.assertTrue(mongoCluster instanceof MongoClusterGetOutputEntity);

        String data = GsonUtils.t2Json(mongoCluster);
        System.out.println("-------------Get mongo cluster begin------------------------------");
        System.out.println(data);
        System.out.println("-------------Get mongo cluster end------------------------------");
    }

    @Test
    public void testPostHandleSubEnv() {
        EnvProfile profile = new EnvProfile(env, subEnv);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(profile).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_CLUSTER_NAME)).andReturn(clusterName).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();
        EasyMock.replay(request);

        PluginResult result = handler.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);

        MongoClusterGetOutputEntity mongoCluster = (MongoClusterGetOutputEntity) result.getAttribute();
        Assert.assertTrue(mongoCluster instanceof MongoClusterGetOutputEntity);

        String data = GsonUtils.t2Json(mongoCluster);
        System.out.println("-------------Get mongo cluster begin------------------------------");
        System.out.println(data);
        System.out.println("-------------Get mongo cluster end------------------------------");
    }

    //    @Test
    public void testPostHandleNotExist() {
        EnvProfile profile = new EnvProfile(env, subEnv);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(profile).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_CLUSTER_NAME)).andReturn(clusterName + "test").anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();
        EasyMock.replay(request);

        boolean isSuccess = true;
        try {
            PluginResult result = handler.postHandle(request);
        } catch (Exception e) {
            System.out.println("exception:" + e.getMessage());
            isSuccess = false;
        }
        assert !isSuccess;
    }

    //    @Test
    public void testPostHandleContentEmpty() {
        EnvProfile profile = new EnvProfile(env, subEnv);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(profile).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_CLUSTER_NAME)).andReturn(clusterName + "test").anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();
        EasyMock.replay(request);

        PluginResult result = handler.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);
        Assert.assertNull(result.getAttribute());
    }

    @Test
    public void testPostHandle2() {
        EnvProfile profile = new EnvProfile(env, subEnv);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(profile).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_CLUSTER_NAME)).andReturn(clusterName).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.2").anyTimes();
        EasyMock.replay(request);

        PluginResult result = handler.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertNotEquals(result.getCode(), PluginStatusCode.OK);
    }

}