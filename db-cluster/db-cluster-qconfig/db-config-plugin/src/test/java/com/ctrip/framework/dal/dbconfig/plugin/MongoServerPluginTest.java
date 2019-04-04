package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import qunar.tc.qconfig.common.util.Constants;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.MongoConstants.*;


/**
 * Created by shenjie on 2019/4/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoServerPlugin.class})
public class MongoServerPluginTest {

    private MongoServerPlugin mongoServerPlugin = new MongoServerPlugin();
    private HttpServletRequest request;
    private String clusterName = "diuserprofile-diuserprofiledb";
    private String env = "fat";

    @Before
    public void init() throws Exception {
        QconfigService qconfigService = new MockQconfigService();
        mongoServerPlugin.init(qconfigService);

        //创建request的Mock
        request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter(Constants.GROUP_NAME)).andReturn(MONGO_CLIENT_APP_ID).anyTimes();

        initForGetConfig();
//        initForForceLoad();
    }


    @Test
    public void preHandle() throws Exception {
        request.setAttribute(EasyMock.eq(REQ_ATTR_CLUSTER_NAME), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.expect(request.getScheme()).andReturn(REQUEST_SCHEMA_HTTPS).anyTimes();
        EasyMock.replay(request);   //保存期望结果

        String groupId = MONGO_CLIENT_APP_ID;
        String dataId = clusterName;
        String profile = "fat:LPT10";
        ConfigDetail configDetail = new ConfigDetail(groupId, dataId, profile);
        WrappedRequest wrappedRequest = new WrappedRequest(request, configDetail);
        PluginResult pluginResult = mongoServerPlugin.preHandle(wrappedRequest);
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

        String groupId = MONGO_CLIENT_APP_ID;
        String dataId = clusterName;
        //String profile = "fat:LPT10";
        long version = 1L;
        String content = buildMongoClusterContent();
        ConfigDetail configDetail = new ConfigDetail(groupId, dataId, profile, version, content);
        WrappedRequest wrappedRequest = new WrappedRequest(request, configDetail);
        PluginResult pluginResult = mongoServerPlugin.postHandle(wrappedRequest);
        assert (pluginResult != null);
        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());
        //assert(pluginResult.getAttribute() != null);
        System.out.println("pluginResult.attribute=" + GsonUtils.Object2Json(pluginResult.getAttribute()));
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

    //build test content
    private String buildMongoClusterContent() {
        return "{\n" +
                "  \"clusterName\": \"diuserprofile-diuserprofiledb\",\n" +
                "  \"clusterType\": \"REPLICATION\",\n" +
                "  \"dbName\": \"testDBtestDBtestDBtestDB\",\n" +
                "  \"userId\": \"DD326CA3D8F038641D6A7FF9D3948BD0\",\n" +
                "  \"password\": \"1A08EF1DDB2951B79EBA0839072FBEBB02A9A77FCF74D09CCDA2ECC6C7E6C17B\",\n" +
                "  \"nodes\": [\n" +
                "    {\n" +
                "      \"host\": \"bridge.soa.uat.qa.nt.ctripcorp.com\",\n" +
                "      \"port\": 65535\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    @Test
    public void registerPoints() throws Exception {
    }

}