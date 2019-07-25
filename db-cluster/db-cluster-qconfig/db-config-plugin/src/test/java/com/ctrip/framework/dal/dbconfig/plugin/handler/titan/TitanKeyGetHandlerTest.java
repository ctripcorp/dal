package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.KeyGetOutputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandler;
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
 * @author c7ch23en
 */
public class TitanKeyGetHandlerTest implements TitanConstants {

    private String titanKey = "titantest_lzyan_v_01";
    private String env = "fat";
    private HttpServletRequest request;
    private AdminHandler handler;

    public TitanKeyGetHandlerTest() {
        QconfigService service = new MockQconfigService();
        PluginConfigManager pluginConfigManager = PluginConfigManager.getInstance(service);
        handler = new TitanKeyGetHandler(service, pluginConfigManager);
    }

    @Before
    public void beforeTest() {
        request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter(REQ_PARAM_ENV)).andReturn(env).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_TITAN_KEY)).andReturn(titanKey).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_TARGET_APPID)).andReturn(TITAN_QCONFIG_KEYS_APPID).anyTimes();
    }

    @Test
    public void testPreHandle() {
        request.setAttribute(EasyMock.eq(REQ_ATTR_TITAN_KEY), EasyMock.anyString());
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
        EasyMock.expect(request.getAttribute(REQ_ATTR_TITAN_KEY)).andReturn(titanKey).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.2").anyTimes();
        EasyMock.replay(request);

        PluginResult result = handler.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertNotEquals(result.getCode(), PluginStatusCode.OK);
    }

    @Test
    public void testPostHandle2() {
        EnvProfile profile = new EnvProfile(env);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(profile).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_TITAN_KEY)).andReturn(titanKey).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();
        EasyMock.replay(request);

        PluginResult result = handler.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);
        Assert.assertTrue(result.getAttribute() instanceof KeyGetOutputEntity);
    }

}
