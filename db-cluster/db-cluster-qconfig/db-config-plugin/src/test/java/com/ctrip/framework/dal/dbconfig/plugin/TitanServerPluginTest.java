package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import com.google.common.base.Strings;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import qunar.tc.qconfig.common.util.Constants;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.*;

/**
 * Created by shenjie on 2019/4/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TitanServerPlugin.class})
public class TitanServerPluginTest {

    private TitanServerPlugin titanServerPlugin = new TitanServerPlugin();
    private HttpServletRequest request;
    private String titanKey = "titantest_lzyan_v_01";
    private String env = "fat";

    @Before
    public void init() throws Exception {
        QconfigService qconfigService = new MockQconfigService();
        titanServerPlugin.init(qconfigService);

        //创建request的Mock
        request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter(Constants.GROUP_NAME)).andReturn(TITAN_QCONFIG_KEYS_APPID).anyTimes();

        initForGetConfig();
//        initForForceLoad();
    }

    @Test
    public void preHandle() throws Exception {
        request.setAttribute(EasyMock.eq(REQ_ATTR_TITAN_KEY), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.expect(request.getScheme()).andReturn(REQUEST_SCHEMA_HTTPS).anyTimes();
        EasyMock.replay(request);   //保存期望结果

        String groupId = TITAN_QCONFIG_KEYS_APPID;
        String dataId = "titantest_lzyan_v_01";
        String profile = "fat:LPT10";
        ConfigDetail configDetail = new ConfigDetail(groupId, dataId, profile);
        WrappedRequest wrappedRequest = new WrappedRequest(request, configDetail);
        PluginResult pluginResult = titanServerPlugin.preHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        assert (pluginResult.getCode() == PluginStatusCode.OK);
    }

    @Test
    public void postHandle() throws Exception {
        String profile = CommonHelper.formatProfileFromEnv(env);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(new EnvProfile(env)).anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn("10.5.1.174").anyTimes();
        EasyMock.replay(request);   //保存期望结果
        EasyMock.verify(request);

        String groupId = TITAN_QCONFIG_KEYS_APPID;
        String dataId = "titantest_lzyan_v_01";
        //String profile = "fat:LPT10";
        long version = 1L;
        String content = buildTitanKeyContent(titanKey);
        ConfigDetail configDetail = new ConfigDetail(groupId, dataId, profile, version, content);
        WrappedRequest wrappedRequest = new WrappedRequest(request, configDetail);
        PluginResult pluginResult = titanServerPlugin.postHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        //assert(pluginResult.getAttribute() != null);
        System.out.println("pluginResult.attribute=" + GsonUtils.Object2Json(pluginResult.getAttribute()));
    }

    @Test
    public void registerPoints() throws Exception {
    }

    //init for <>
    private void initForGetConfig() {
        EasyMock.expect(request.getRequestURI()).andReturn("/client/getconfigv2").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
    }

    //init for <>
    private void initForForceLoad() {
        EasyMock.expect(request.getRequestURI()).andReturn("/client/forceloadv2").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
    }

    //build test titanKey content
    private String buildTitanKeyContent(String keyName) {
        if (Strings.isNullOrEmpty(keyName)) {
            keyName = "titantest_lzyan_v_01";
        }
        String returnFlag = "\n";
        StringBuilder sb = new StringBuilder();
        sb.append("sslCode=VZ00000000000441").append(returnFlag);
        sb.append("keyName=").append(keyName).append(returnFlag);
        sb.append("serverName=mysqldaltest01.mysql.db.fat.qa.nt.ctripcorp.com").append(returnFlag);
        sb.append("serverIp=10.2.74.111").append(returnFlag);
        sb.append("port=55111").append(returnFlag);
        sb.append("uid=DD326CA3D8F038641D6A7FF9D3948BD0").append(returnFlag);
        sb.append("password=1A08EF1DDB2951B79EBA0839072FBEBB02A9A77FCF74D09CCDA2ECC6C7E6C17B").append(returnFlag);
        sb.append("dbName=mysqldaltest01db").append(returnFlag);
        sb.append("providerName=MySql.Data.MySqlClient").append(returnFlag);
        sb.append("enabled=true").append(returnFlag);
        sb.append("permissions=100007326").append(returnFlag);
        sb.append("updateUser=lzyan").append(returnFlag);
        sb.append("createUser=lzyan").append(returnFlag);
        sb.append("timeOut=30").append(returnFlag);
        sb.append("extParam=").append(returnFlag);
        sb.append("version=2").append(returnFlag);
        return sb.toString();
    }

}