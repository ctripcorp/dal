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
    private String proEnv = "pro";
    private String awsProfile = "pro:fra-aws";
    private String privateNetIp = "10.5.156.193";
    private String publicNetIp = "10.9.253.50";
    private String qconfigAgencyIp = "1.1.1.1";
    private String ttToken = "fseYTdpoOWzdkkS5hcTfVWvuzHgETovQSQwOUMMq2ilm0wDOhRdL+OSbnynbrRgem+7UofvSpF9SgQ1eZrB6aXcgwsxAEFF3KZaXwObQ+ykCn+q4eKfYCMzkSCo1wNBRAgW09vV+194nVccMmkTg8iuo6kQK8XKr4EpMK3V6A8Y=";

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
    public void preHandleHttps() throws Exception {
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
        assert pluginResult.getCode() == PluginStatusCode.OK;
    }

    @Test
    public void preHandlePrivateNet() throws Exception {
        request.setAttribute(EasyMock.eq(REQ_ATTR_TITAN_KEY), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.expect(request.getScheme()).andReturn("http").anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn(privateNetIp).anyTimes();
        EasyMock.expect(request.getHeader(X_REAL_IP)).andReturn(qconfigAgencyIp).anyTimes();//tt-token
        EasyMock.expect(request.getHeader(TT_TOKEN)).andReturn(ttToken).anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PRIVATE_NET_TYPE).anyTimes();
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
    public void preHandlePrivateNetFailed() throws Exception {
        request.setAttribute(EasyMock.eq(REQ_ATTR_TITAN_KEY), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.expect(request.getScheme()).andReturn("http").anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn(privateNetIp).anyTimes();
        EasyMock.expect(request.getHeader(X_REAL_IP)).andReturn(privateNetIp).anyTimes();//tt-token
        EasyMock.expect(request.getHeader(TT_TOKEN)).andReturn(ttToken).anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PRIVATE_NET_TYPE).anyTimes();
        EasyMock.replay(request);   //保存期望结果

        String groupId = TITAN_QCONFIG_KEYS_APPID;
        String dataId = "titantest_lzyan_v_01";
        String profile = "fat:LPT10";
        ConfigDetail configDetail = new ConfigDetail(groupId, dataId, profile);
        WrappedRequest wrappedRequest = new WrappedRequest(request, configDetail);
        PluginResult pluginResult = titanServerPlugin.preHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        assert pluginResult.getCode() != PluginStatusCode.OK;
    }

    @Test
    public void preHandlePublicNet() throws Exception {
        request.setAttribute(EasyMock.eq(REQ_ATTR_TITAN_KEY), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.expect(request.getScheme()).andReturn("http").anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn(publicNetIp).anyTimes();
        EasyMock.expect(request.getHeader(X_REAL_IP)).andReturn("127.0.0.1").anyTimes();//tt-token
        EasyMock.expect(request.getHeader(TT_TOKEN)).andReturn(ttToken).anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PUBLIC_NET_TYPE).anyTimes();
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
    public void preHandlePublicNetFailed() throws Exception {
        request.setAttribute(EasyMock.eq(REQ_ATTR_TITAN_KEY), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.expect(request.getScheme()).andReturn("http").anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn(publicNetIp).anyTimes();
        EasyMock.expect(request.getHeader(X_REAL_IP)).andReturn("").anyTimes();//tt-token
        EasyMock.expect(request.getHeader(TT_TOKEN)).andReturn("").anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PUBLIC_NET_TYPE).anyTimes();
        EasyMock.replay(request);   //保存期望结果

        String groupId = TITAN_QCONFIG_KEYS_APPID;
        String dataId = "titantest_lzyan_v_01";
        String profile = "fat:LPT10";
        ConfigDetail configDetail = new ConfigDetail(groupId, dataId, profile);
        WrappedRequest wrappedRequest = new WrappedRequest(request, configDetail);
        PluginResult pluginResult = titanServerPlugin.preHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        assert (pluginResult.getCode() != PluginStatusCode.OK);
    }

    @Test
    public void postHandlePrivateNet() throws Exception {
        String profile = CommonHelper.formatProfileFromEnv(env);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(new EnvProfile(env)).anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn(privateNetIp).anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PRIVATE_NET_TYPE).anyTimes();
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
        assert pluginResult.getCode() == PluginStatusCode.OK;
        //assert(pluginResult.getAttribute() != null);
        System.out.println("pluginResult.attribute=" + GsonUtils.Object2Json(pluginResult.getAttribute()));
    }

    @Test
    public void postHandlePrivateNetFailed() throws Exception {
        String profile = CommonHelper.formatProfileFromEnv(env);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(new EnvProfile(env)).anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn("10.5.158.50").anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PRIVATE_NET_TYPE).anyTimes();
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
        assert pluginResult.getCode() != PluginStatusCode.OK;
        //assert(pluginResult.getAttribute() != null);
        System.out.println("pluginResult.attribute=" + GsonUtils.Object2Json(pluginResult.getAttribute()));
    }

    @Test
    public void postHandlePublicNet() throws Exception {
        String profile = CommonHelper.formatProfileFromEnv(env);
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(new EnvProfile(env)).anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn(publicNetIp).anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PUBLIC_NET_TYPE).anyTimes();
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
        assert (pluginResult.getCode() == PluginStatusCode.OK);
        //assert(pluginResult.getAttribute() != null);
        System.out.println("pluginResult.attribute=" + GsonUtils.Object2Json(pluginResult.getAttribute()));
    }

    @Test
    public void postHandleFatSubEnvDisabled() throws Exception {
        String profile = CommonHelper.formatProfileFromEnv("fat:fat1");
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(new EnvProfile("fat:fat1")).anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn(privateNetIp).anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PRIVATE_NET_TYPE).anyTimes();
        EasyMock.replay(request);   //保存期望结果
        EasyMock.verify(request);

        String groupId = TITAN_QCONFIG_KEYS_APPID;
        String dataId = "titantest_lzyan_v_01";
        long version = 1L;
        String content = buildTitanKeyContent(titanKey, false);
        ConfigDetail configDetail = new ConfigDetail(groupId, dataId, profile, version, content);
        WrappedRequest wrappedRequest = new WrappedRequest(request, configDetail);
        PluginResult pluginResult = titanServerPlugin.postHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        assert pluginResult.getCode() == PluginStatusCode.OK;
    }

    @Test
    public void postHandleProSubEnvDisabled() throws Exception {
        String profile = CommonHelper.formatProfileFromEnv("pro:fra-aws");
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(new EnvProfile("pro:fra-aws")).anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn(privateNetIp).anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PRIVATE_NET_TYPE).anyTimes();
        EasyMock.replay(request);   //保存期望结果
        EasyMock.verify(request);

        String groupId = TITAN_QCONFIG_KEYS_APPID;
        String dataId = "titantest_lzyan_v_01";
        long version = 1L;
        String content = buildTitanKeyContent(titanKey, false);
        ConfigDetail configDetail = new ConfigDetail(groupId, dataId, profile, version, content);
        WrappedRequest wrappedRequest = new WrappedRequest(request, configDetail);
        PluginResult pluginResult = titanServerPlugin.postHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        assert pluginResult.getCode() == PluginStatusCode.TITAN_KEY_DISABLE;
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

    private String buildTitanKeyContent(String keyName) {
        return buildTitanKeyContent(keyName, true);
    }

    //build test titanKey content
    private String buildTitanKeyContent(String keyName, boolean enabled) {
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
        sb.append("enabled=" + enabled).append(returnFlag);
        sb.append("permissions=100020032").append(returnFlag);
        sb.append("updateUser=lzyan").append(returnFlag);
        sb.append("createUser=lzyan").append(returnFlag);
        sb.append("timeOut=30").append(returnFlag);
        sb.append("extParam=").append(returnFlag);
        sb.append("version=2").append(returnFlag);
        return sb.toString();
    }

    @Test
    public void postHandleAwsTest() throws Exception {
        String profile = proEnv;
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(new EnvProfile(awsProfile)).anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn(privateNetIp).anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PRIVATE_NET_TYPE).anyTimes();
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
        assert pluginResult.getCode() != PluginStatusCode.OK;
        System.out.println("pluginResult.message=" + pluginResult.getMessage());
    }

    @Test
    public void postHandleLptTest() throws Exception {
        String profile = "fat";
        EasyMock.expect(request.getAttribute(REQ_ATTR_ENV_PROFILE)).andReturn(new EnvProfile("fat:lpt")).anyTimes();
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn(privateNetIp).anyTimes();
        EasyMock.expect(request.getHeader(HEADER_NET_TYPE)).andReturn(PRIVATE_NET_TYPE).anyTimes();
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
        assert pluginResult.getCode() != PluginStatusCode.OK;
        System.out.println("pluginResult.message=" + pluginResult.getMessage());
    }

}