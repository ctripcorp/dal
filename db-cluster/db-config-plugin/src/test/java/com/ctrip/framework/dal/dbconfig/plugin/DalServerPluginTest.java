package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.KeyInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure.*;
import com.ctrip.framework.dal.dbconfig.plugin.service.DefaultDataSourceCrypto;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.DalClusterUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import com.ctrip.framework.dal.dbconfig.plugin.util.XmlUtils;
import com.google.common.collect.Lists;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import qunar.tc.qconfig.common.util.Constants;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.DalConstants.*;

/**
 * Created by shenjie on 2019/5/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TitanServerPlugin.class})
public class DalServerPluginTest {
    private DalServerPlugin dalServerPlugin = new DalServerPlugin();
    private HttpServletRequest request;
    private String clusterName = "demoCluster";
    private String env = "fat";
    private String sslCode = "VZ00000000000441";
    private String operator = "testUser";

    @Before
    public void init() throws Exception {
        QconfigService qconfigService = new MockQconfigService();
        dalServerPlugin.init(qconfigService);

        //创建request的Mock
        request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter(Constants.GROUP_NAME)).andReturn(CLUSTER_CONFIG_STORE_APP_ID).anyTimes();

        initForGetConfig();
//        initForForceLoad();
    }

    @Test
    public void preHandle() throws Exception {
        request.setAttribute(EasyMock.eq(REQ_ATTR_CLUSTER_NAME), EasyMock.anyString());
        request.setAttribute(EasyMock.eq(REQ_ATTR_ENV_PROFILE), EasyMock.anyString());
        EasyMock.expect(request.getScheme()).andReturn(REQUEST_SCHEMA_HTTPS).anyTimes();
        EasyMock.replay(request);   //保存期望结果

        String groupId = CLUSTER_CONFIG_STORE_APP_ID;
        String dataId = clusterName;
        String profile = "fat:LPT10";
        ConfigDetail configDetail = new ConfigDetail(groupId, dataId, profile);
        WrappedRequest wrappedRequest = new WrappedRequest(request, configDetail);
        PluginResult pluginResult = dalServerPlugin.preHandle(wrappedRequest);
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

        String groupId = CLUSTER_CONFIG_STORE_APP_ID;
        String dataId = clusterName;
        //String profile = "fat:LPT10";
        long version = 1L;
        String content = buildClientConfig();
        ConfigDetail configDetail = new ConfigDetail(groupId, dataId, profile, version, content);
        WrappedRequest wrappedRequest = new WrappedRequest(request, configDetail);
        PluginResult pluginResult = dalServerPlugin.postHandle(wrappedRequest);
        assert (pluginResult != null);
//        System.out.println("pluginResult.code=" + pluginResult.getCode() + ", pluginResult.message=" + pluginResult.getMessage());

        String clientContent = pluginResult.getConfigs().get(0).getContent();
        System.out.println("------------------------decrypted client content begin------------------------");
        System.out.print(clientContent);
        System.out.println("------------------------decrypted client content end------------------------");

        DalConfigure configure = (DalConfigure) XmlUtils.fromXml(clientContent, DalConfigure.class);
        assert configure != null;
    }

    @Test
    public void registerPoints() throws Exception {
    }

    private void initForGetConfig() {
        EasyMock.expect(request.getRequestURI()).andReturn("/client/getconfigv2").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
    }

    private void initForForceLoad() {
        EasyMock.expect(request.getRequestURI()).andReturn("/client/forceloadv2").anyTimes();
        EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
    }

    private String buildClientConfig() throws Exception {
        String encryptedUid = encrypt("user_w");
        String encryptedPassword = encrypt("123456");

        Database database = new Database("master", "127.0.0.1", 8080, "demoDbShard01",
                encryptedUid, encryptedPassword, 1, "sun");
        DatabaseShard databaseShard = new DatabaseShard(0, "masterDomain", "slaveDomain", 8080, 8080,
                "masterTitanKey", "slaveTitanKey", Lists.newArrayList(database, database));

        DatabaseShards databaseShards = new DatabaseShards(Lists.newArrayList(databaseShard, databaseShard));
        Cluster cluster = new Cluster("demoCluster", "mysql", 1, databaseShards);
        cluster.setSslCode(sslCode);
        cluster.setOperator(operator);
        cluster.setUpdateTime(DalClusterUtils.formatDate(new Date()));

        DalConfigure configure = new DalConfigure(cluster);

        String configContent = XmlUtils.toXml(configure);

        System.out.println("------------------------encrypted client content begin------------------------");
        System.out.print(configContent);
        System.out.println("------------------------encrypted client content end------------------------");

        return configContent;
    }

    public String encrypt(String content) throws Exception {
        KeyInfo keyInfo = buildKeyInfo();
        String cipherText = DefaultDataSourceCrypto.getInstance().encrypt(content, keyInfo);
        return cipherText;
    }

    private KeyInfo buildKeyInfo() {
        String mKey = "SU5HMSIwIAYDVQQD";
        String mSslCode = "VZ00000000000441";
        KeyInfo key = new KeyInfo();
        key.setKey(mKey).setSslCode(mSslCode);
        return key;
    }
}
