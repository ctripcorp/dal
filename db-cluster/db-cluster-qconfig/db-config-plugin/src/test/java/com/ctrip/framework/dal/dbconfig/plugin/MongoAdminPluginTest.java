package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.constant.MongoConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.util.MockQconfigService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shenjie on 2019/4/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoAdminPluginTest.class})
public class MongoAdminPluginTest implements MongoConstants {

    private MongoAdminPlugin mongoAdminPlugin = new MongoAdminPlugin();
    private HttpServletRequest request;
    private String operator = "test";
    private String env = "fat";

    @Before
    public void init() throws Exception {
        QconfigService qconfigService = new MockQconfigService();
        mongoAdminPlugin.init(qconfigService);

        //创建request的Mock
        request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter(REQ_PARAM_ENV)).andReturn(env).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_OPERATOR)).andReturn(operator).anyTimes();

        initForMongoClusterPostHandler();
    }

    @Test
    public void preHandle() throws Exception {
        request.setAttribute(EasyMock.eq(REQ_ATTR_OPERATOR), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.replay(request);   //保存期望结果

        WrappedRequest wrappedRequest = new WrappedRequest(request);
        PluginResult pluginResult = mongoAdminPlugin.preHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        assert (pluginResult.getCode() == PluginStatusCode.OK);
    }

    @Test
    public void postHandle() throws Exception {
        String profile = CommonHelper.formatProfileFromEnv(env);
        EasyMock.expect(request.getAttribute(REQ_ATTR_OPERATOR)).andReturn(operator).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(new EnvProfile(profile, "FAT1")).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();

        EasyMock.replay(request);   //保存期望结果
        EasyMock.verify(request);

        WrappedRequest wrappedRequest = new WrappedRequest(request);
        PluginResult pluginResult = mongoAdminPlugin.postHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        //assert(pluginResult.getAttribute() != null);
        System.out.println("pluginResult.attribute=" + GsonUtils.Object2Json(pluginResult.getAttribute()));
    }

    @Test
    public void registerPoints() throws Exception {
    }

    //init for <TitanKeyPostHandler>
    private void initForMongoClusterPostHandler() {
        EasyMock.expect(request.getRequestURI()).andReturn("/plugins/mongo/config").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("POST").anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_SUB_ENV)).andReturn("FAT1").anyTimes();
        EasyMock.expect(request.getContentLength()).andReturn(0).anyTimes();
    }
}