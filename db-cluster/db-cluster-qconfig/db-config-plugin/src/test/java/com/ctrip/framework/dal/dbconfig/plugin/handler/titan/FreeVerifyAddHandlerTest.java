package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.TitanAdminPlugin;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
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
 * Created by shenjie on 2019/4/12.
 */
public class FreeVerifyAddHandlerTest implements TitanConstants {
    private String titanKey = "titantest_lzyan_v_01";
    private String env = "fat";
    private String subEnv = "fat1";
    private HttpServletRequest request;
    private static TitanAdminPlugin titanAdminPlugin = new TitanAdminPlugin();

    public FreeVerifyAddHandlerTest() {
        QconfigService service = new MockQconfigService();
        titanAdminPlugin.init(service);
    }

    @Before
    public void beforeTest() {
        request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter(REQ_PARAM_ENV)).andReturn(env).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_SUB_ENV)).andReturn(subEnv).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_TITAN_KEY)).andReturn(titanKey).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_TARGET_APPID)).andReturn(TITAN_QCONFIG_KEYS_APPID).anyTimes();

        EasyMock.expect(request.getRequestURI()).andReturn("/plugins/titan/freeverify/add").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("POST").anyTimes();

        try {
            String request = "{\n" +
                    "   \"titanKeyList\":\"titantest_lzyan_v_01\",\n" +
                    "   \"freeVerifyAppIdList\":\"100019729\"\n" +
                    "}";
            EasyMock.expect(this.request.getContentLength()).andReturn(request.length()).anyTimes();
            EasyMock.expect(this.request.getInputStream()).andReturn(
                    new DelegatingServletInputStream(new ByteArrayInputStream(request.getBytes())
                    )).anyTimes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPreHandle() {
        request.setAttribute(EasyMock.eq(REQ_ATTR_TITAN_KEY), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.replay(request);

        PluginResult result = titanAdminPlugin.preHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);
    }

    @Test
    public void testPostHandleSuccess() {
        EnvProfile profile = new EnvProfile(env);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(profile).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_TITAN_KEY)).andReturn(titanKey).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();
        EasyMock.replay(request);

        PluginResult result = titanAdminPlugin.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);
    }
}
