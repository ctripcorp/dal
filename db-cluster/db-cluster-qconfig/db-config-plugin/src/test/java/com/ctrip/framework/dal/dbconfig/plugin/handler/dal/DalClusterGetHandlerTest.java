package com.ctrip.framework.dal.dbconfig.plugin.handler.dal;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DalClusterEntity;
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

import static com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants.*;

/**
 * Created by shenjie on 2019/8/9.
 */
public class DalClusterGetHandlerTest {
    private String env = "fat";
    private String subEnv = "fat22";
    private String operator = "testUser";
    private String clusterName = "dalClusterTest";
    private HttpServletRequest request;
    private AdminHandler handler;

    public DalClusterGetHandlerTest() {
        QconfigService service = new MockQconfigService();
        PluginConfigManager pluginConfigManager = PluginConfigManager.getInstance(service);
        handler = new DalClusterGetHandler(service, pluginConfigManager);
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
    public void testPostHandle1() {
        EnvProfile profile = new EnvProfile(env);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(profile).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_CLUSTER_NAME)).andReturn(clusterName).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();
        EasyMock.replay(request);

        PluginResult result = handler.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);

        DalClusterEntity dalCluster = (DalClusterEntity) result.getAttribute();
        Assert.assertTrue(dalCluster instanceof DalClusterEntity);

        String data = GsonUtils.t2Json(dalCluster);
        System.out.println("-------------Get dal cluster begin------------------------------");
        System.out.println(data);
        System.out.println("-------------Get dal cluster end------------------------------");
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

        DalClusterEntity dalCluster = (DalClusterEntity) result.getAttribute();
        Assert.assertTrue(dalCluster instanceof DalClusterEntity);

        String data = GsonUtils.t2Json(dalCluster);
        System.out.println("-------------Get dal cluster begin------------------------------");
        System.out.println(data);
        System.out.println("-------------Get dal cluster end------------------------------");
    }

    @Test
    public void testPostHandle2() {
        EnvProfile profile = new EnvProfile(env, subEnv);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(profile).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_CLUSTER_NAME)).andReturn(clusterName).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.10").anyTimes();
        EasyMock.replay(request);

        PluginResult result = handler.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertNotEquals(result.getCode(), PluginStatusCode.OK);
    }
}