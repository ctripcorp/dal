package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import qunar.tc.qconfig.common.util.Constants;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.*;

/**
 * Created by shenjie on 2019/4/2.
 */
public class TitanAdminPluginTest {

    private static TitanAdminPlugin titanAdminPlugin = new TitanAdminPlugin();
    private HttpServletRequest request;
    private String titanKey = "titantest_lzyan_v_01";
    private String env = "fat";

    @BeforeClass
    public static void initClass() {
        QconfigService qconfigService = new MockQconfigService();
        titanAdminPlugin.init(qconfigService);
    }

    @Before
    public void init() throws Exception {
        //创建request的Mock
        request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter(REQ_PARAM_ENV)).andReturn(env).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_TITAN_KEY)).andReturn(titanKey).anyTimes();
        EasyMock.expect(request.getParameter(Constants.GROUP_NAME)).andReturn(TITAN_QCONFIG_KEYS_APPID).anyTimes();
        EasyMock.expect(request.getParameter(REQ_PARAM_TARGET_APPID)).andReturn(TITAN_QCONFIG_KEYS_APPID).anyTimes();

//        initForTitanKeyPostHandler();
//        initForTitanKeyListByTimeHandler();
//        initForTitanKeyListHandler();
//        initForTitanKeyGetHandler();
//        initForTitanKeyMHAUpdateHandler();
//        initForTitanKeySSLCodeGetHandler();
//        initForTitanKeySSLCodeUpdateHandler();
        initForTitanKeyForceDataWashHandler();
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
        Thread.currentThread().join();
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

    //init for <TitanKeyGetHandler>
    private void initForTitanKeyGetHandler() {
        EasyMock.expect(request.getRequestURI()).andReturn("/plugins/titan/config").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
    }

    //init for <TitanKeyListByTimeHandler>
    private void initForTitanKeyListByTimeHandler() {
        EasyMock.expect(request.getRequestURI()).andReturn("/plugins/titan/configs/bytime").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
        EasyMock.expect(request.getParameter(BEGIN_TIME)).andReturn("2018-06-04 19:30:00").anyTimes();
    }

    //init for <TitanKeyListHandler>
    private void initForTitanKeyListHandler() {
        EasyMock.expect(request.getRequestURI()).andReturn("/plugins/titan/configs").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
        EasyMock.expect(request.getParameter("pageNo")).andReturn("1").anyTimes();
        EasyMock.expect(request.getParameter("pageSize")).andReturn("10").anyTimes();
    }

    //init for <TitanKeyMHAUpdateHandler>
    private void initForTitanKeyMHAUpdateHandler() {
        EasyMock.expect(request.getRequestURI()).andReturn("/plugins/titan/config/mha").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("POST").anyTimes();
        //EasyMock.expect(request.getContentLength()).andReturn(0).anyTimes();

        try {
            String mhaBody = "{\"env\": \"fat\", \"data\": [{\"keyname\": \"" + titanKey + "\", \"server\": \"10.2.74.122\", \"port\": 55111}]}";
            EasyMock.expect(request.getContentLength()).andReturn(mhaBody.length()).anyTimes();
            EasyMock.expect(request.getInputStream()).andReturn(
                    new DelegatingServletInputStream(new ByteArrayInputStream(mhaBody.getBytes())
                    )).anyTimes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //init for <TitanKeySSLCodeGetHandler>
    private void initForTitanKeySSLCodeGetHandler() {
        EasyMock.expect(request.getRequestURI()).andReturn("/plugins/titan/sslcode").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
    }

    //init for <TitanKeySSLCodeUpdateHandler>
    private void initForTitanKeySSLCodeUpdateHandler() {
        EasyMock.expect(request.getRequestURI()).andReturn("/plugins/titan/sslcode").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("POST").anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_USER)).andReturn("lzyan").anyTimes();
        try {
            String newSslCode = "SQ00000000001368";
            EasyMock.expect(request.getContentLength()).andReturn(newSslCode.length()).anyTimes();
            EasyMock.expect(request.getInputStream()).andReturn(
                    new DelegatingServletInputStream(new ByteArrayInputStream(newSslCode.getBytes())
                    )).anyTimes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //init for <TitanKeyForceDataWashHandler>
    private void initForTitanKeyForceDataWashHandler() {
        EasyMock.expect(request.getRequestURI()).andReturn("/plugins/titan/config/datawash").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_USER)).andReturn("lzyan").anyTimes();
    }

}