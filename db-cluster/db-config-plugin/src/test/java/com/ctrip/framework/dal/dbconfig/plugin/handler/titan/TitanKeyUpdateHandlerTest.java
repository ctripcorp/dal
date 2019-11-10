package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.MhaInputBasicData;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.TitanUpdateBasicData;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.TitanUpdateInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import com.google.common.collect.Lists;
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
 * Created by shenjie on 2019/6/26.
 */
public class TitanKeyUpdateHandlerTest implements TitanConstants {

    private String env = "fat";
    private HttpServletRequest request;
    private AdminHandler handler;

    public TitanKeyUpdateHandlerTest() {
        QconfigService service = new MockQconfigService();
        PluginConfigManager pluginConfigManager = PluginConfigManager.getInstance(service);
        handler = new TitanKeyUpdateHandler(service, pluginConfigManager, new KeyListByDbNameHandler(service, pluginConfigManager),
                new TitanKeyMHAUpdateHandler(service, pluginConfigManager));
    }

    @Before
    public void beforeTest() {
        request = EasyMock.createMock(HttpServletRequest.class);

        try {
            TitanUpdateBasicData basicData = new TitanUpdateBasicData();
            basicData.setDbName("mysqldaltest01db");
            basicData.setDomain("mysqldaltest01.mysql.db.fat.qa.nt.ctripcorp.com");
            basicData.setIp("10.2.74.122");
            basicData.setPort(55111);

            MhaInputBasicData mhaInputBasicData = new MhaInputBasicData();
            mhaInputBasicData.setKeyname("mysqldaltest02db");
            mhaInputBasicData.setServer("10.2.74.122");
            mhaInputBasicData.setPort(55111);

            TitanUpdateInputEntity titanUpdateInputEntity = new TitanUpdateInputEntity();
            titanUpdateInputEntity.setEnv(env);
            titanUpdateInputEntity.setDbData(Lists.newArrayList(basicData));
            titanUpdateInputEntity.setMhaData(Lists.newArrayList(mhaInputBasicData));

            String requestBody = GsonUtils.t2Json(titanUpdateInputEntity);
            EasyMock.expect(this.request.getContentLength()).andReturn(requestBody.length()).anyTimes();
            EasyMock.expect(this.request.getInputStream()).andReturn(
                    new DelegatingServletInputStream(new ByteArrayInputStream(requestBody.getBytes()))).anyTimes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPreHandle() {
        EasyMock.replay(request);
        PluginResult result = handler.preHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);
    }

    @Test
    public void testPostHandle1() {
        EasyMock.expect(request.getAttribute(PluginConstant.REMOTE_IP)).andReturn("127.0.0.1").anyTimes();
        EasyMock.replay(request);

        PluginResult result = handler.postHandle(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCode(), PluginStatusCode.OK);
    }

}