package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.util.MockQconfigService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import qunar.tc.qconfig.common.util.Constants;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shenjie on 2019/4/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TitanAdminPlugin.class})
public class TitanAdminPluginTest implements TitanConstants {

    @Autowired
    TitanAdminPlugin titanAdminPlugin;
    private HttpServletRequest request;
    private String titanKey = "titantest_lzyan_v_01";
    private String env = "fat";

    @Before
    public void init() throws Exception {
        QconfigService qconfigService = new MockQconfigService();
        titanAdminPlugin.init(qconfigService);

        //创建request的Mock
        request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter(REQ_PARAM_ENV)).andReturn(env).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_TITAN_KEY)).andReturn(titanKey).anyTimes();
        EasyMock.expect(request.getParameter(Constants.GROUP_NAME)).andReturn(TITAN_QCONFIG_KEYS_APPID).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_TARGET_APPID)).andReturn(TITAN_QCONFIG_KEYS_APPID).anyTimes();

        initForTitanKeyPostHandler();
    }

    @Test
    public void preHandle() throws Exception {
        request.setAttribute(EasyMock.eq(REQ_ATTR_TITAN_KEY), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.replay(request);   //保存期望结果

        WrappedRequest wrappedRequest = new WrappedRequest(request);
        PluginResult pluginResult = titanAdminPlugin.preHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        assert (pluginResult.getCode() == PluginStatusCode.OK);
    }

    @Test
    public void postHandle() throws Exception {
        String profile = CommonHelper.formatProfileFromEnv(env);
        EasyMock.expect(request.getAttribute(REQ_ATTR_TITAN_KEY)).andReturn(titanKey).anyTimes();
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(new EnvProfile(profile)).anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();

        EasyMock.replay(request);   //保存期望结果
        EasyMock.verify(request);

        WrappedRequest wrappedRequest = new WrappedRequest(request);
        PluginResult pluginResult = titanAdminPlugin.postHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        //assert(pluginResult.getAttribute() != null);
        System.out.println("pluginResult.attribute=" + GsonUtils.Object2Json(pluginResult.getAttribute()));
    }

    @Test
    public void registerPoints() throws Exception {
    }

    //init for <TitanKeyPostHandler>
    private void initForTitanKeyPostHandler() {
        EasyMock.expect(request.getRequestURI()).andReturn("/plugins/titan/config").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("POST").anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_SUB_ENV)).andReturn("FAT1").anyTimes();
        EasyMock.expect(request.getContentLength()).andReturn(0).anyTimes();
    }

}