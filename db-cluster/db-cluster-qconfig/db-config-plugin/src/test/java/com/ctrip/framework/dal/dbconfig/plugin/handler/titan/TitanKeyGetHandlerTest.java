package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
        handler = new TitanKeyGetHandler(service);
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
    }

}
