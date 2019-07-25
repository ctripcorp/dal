package com.ctrip.framework.dal.dbconfig.plugin.handler.mongo;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.MongoConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.mongo.MongoClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.DelegatingServletInputStream;
import qunar.tc.qconfig.plugin.PluginConstant;
import qunar.tc.qconfig.plugin.PluginResult;
import qunar.tc.qconfig.plugin.PluginStatusCode;
import qunar.tc.qconfig.plugin.QconfigService;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;

/**
 * Created by shenjie on 2019/5/29.
 */
public class MongoClusterUpdateHandlerTest implements MongoConstants {
    private String clusterName = "demoMongoCluster";
    private String env = "fat";
    private String subEnv = "fat1";
    private String operator = "test";
    private HttpServletRequest request;
    private AdminHandler handler;

    public MongoClusterUpdateHandlerTest() {
        QconfigService service = new MockQconfigService();
        PluginConfigManager pluginConfigManager = PluginConfigManager.getInstance(service);
        handler = new MongoClusterUpdateHandler(service, pluginConfigManager);
    }

    @Before
    public void beforeTest() {
        request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter(REQ_PARAM_ENV)).andReturn(env).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_SUB_ENV)).andReturn(subEnv).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_OPERATOR)).andReturn(operator).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_CLUSTER_NAME)).andReturn(clusterName).anyTimes();

        try {
            MongoClusterEntity mongoCluster = new MongoClusterEntity();
            mongoCluster.setClusterName(clusterName);
            mongoCluster.setEnabled(false);
            String request = GsonUtils.t2Json(mongoCluster);

            EasyMock.expect(this.request.getContentLength()).andReturn(request.length()).anyTimes();
            EasyMock.expect(this.request.getInputStream()).andReturn(
                    new DelegatingServletInputStream(new ByteArrayInputStream(request.getBytes()))).anyTimes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPreHandle() {
        request.setAttribute(EasyMock.eq(REQ_ATTR_OPERATOR), EasyMock.anyString());
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
        EasyMock.expect(request.getAttribute(REQ_ATTR_OPERATOR)).andReturn(operator).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_CLUSTER_NAME)).andReturn(clusterName).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();
        EasyMock.replay(request);

        PluginResult result = handler.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);
    }

}