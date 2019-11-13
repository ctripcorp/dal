package com.ctrip.framework.dal.dbconfig.plugin.handler;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.mongo.MongoClusterPostHandler;
import com.ctrip.framework.dal.dbconfig.plugin.handler.titan.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import qunar.tc.qconfig.plugin.QconfigService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class AdminHandlerDispatcherTest {

    private AdminHandlerDispatcher dispatcher = new AdminHandlerDispatcher();

    @Test
    public void testRegister1() {
        QconfigService service = new MockQconfigService();
        PluginConfigManager pluginConfigManager = PluginConfigManager.getInstance(service);
        List<AdminHandler> handlers = new ArrayList<>();
        handlers.add(new MongoClusterPostHandler(service, pluginConfigManager));
        handlers.add(new MongoClusterPostHandler(service, pluginConfigManager));
        try {
            register(handlers);
            Assert.fail();
        } catch (DbConfigPluginException e) {
            Assert.assertTrue(true);
        } catch (Exception ex) {
            Assert.fail();
        }
    }

    @Test
    public void testRegister2() {
        QconfigService service = new MockQconfigService();
        PluginConfigManager pluginConfigManager = PluginConfigManager.getInstance(service);
        List<AdminHandler> handlers = new ArrayList<>();
        handlers.add(new MongoClusterPostHandler(service, pluginConfigManager));
        handlers.add(new DbNameListHandler(service, pluginConfigManager));
        handlers.add(new FreeVerifyAddHandler(service, pluginConfigManager));
        handlers.add(new FreeVerifyDeleteHandler(service, pluginConfigManager));
        handlers.add(new IndexBuildHandler(service, pluginConfigManager));
        handlers.add(new KeyListByDbNameHandler(service, pluginConfigManager));
        handlers.add(new TitanKeyForceDataWashHandler(service, pluginConfigManager));
        handlers.add(new TitanKeyGetHandler(service, pluginConfigManager));
        handlers.add(new TitanKeyListByTimeHandler(service, pluginConfigManager));
        handlers.add(new TitanKeyListHandler(service, pluginConfigManager));
        handlers.add(new TitanKeyMHAUpdateHandler(service, pluginConfigManager));
        handlers.add(new TitanKeyPermissionMergeHandler(service, pluginConfigManager));
        handlers.add(new TitanKeyPostHandler(service, pluginConfigManager));
        handlers.add(new TitanKeySSLCodeGetHandler(service, pluginConfigManager));
        handlers.add(new TitanKeySSLCodeUpdateHandler(service, pluginConfigManager));
        handlers.add(new WhiteListAddHandler(service, pluginConfigManager));
        handlers.add(new WhiteListDeleteHandler(service, pluginConfigManager));
        register(handlers);

        Assert.assertTrue(getHandler("/plugins/mongo/config/add", "POST") instanceof MongoClusterPostHandler);
        Assert.assertTrue(getHandler("/plugins/titan/whitelist/listAllDbName", "GET") instanceof DbNameListHandler);
        Assert.assertTrue(getHandler("/plugins/titan/freeverify/add", "POST") instanceof FreeVerifyAddHandler);
        Assert.assertTrue(getHandler("/plugins/titan/freeverify/delete", "POST") instanceof FreeVerifyDeleteHandler);
        Assert.assertTrue(getHandler("/plugins/titan/index/build", "GET") instanceof IndexBuildHandler);
        Assert.assertTrue(getHandler("/plugins/titan/whitelist/listTitanKey", "GET") instanceof KeyListByDbNameHandler);
        Assert.assertTrue(getHandler("/plugins/titan/config/datawash", "GET") instanceof TitanKeyForceDataWashHandler);
        Assert.assertTrue(getHandler("/plugins/titan/config", "GET") instanceof TitanKeyGetHandler);
        Assert.assertTrue(getHandler("/plugins/titan/configs/bytime", "GET") instanceof TitanKeyListByTimeHandler);
        Assert.assertTrue(getHandler("/plugins/titan/configs", "GET") instanceof TitanKeyListHandler);
        Assert.assertTrue(getHandler("/plugins/titan/config/mha", "POST") instanceof TitanKeyMHAUpdateHandler);
        Assert.assertTrue(getHandler("/plugins/titan/config/permission/merge", "POST") instanceof TitanKeyPermissionMergeHandler);
        Assert.assertTrue(getHandler("/plugins/titan/config", "POST") instanceof TitanKeyPostHandler);
        Assert.assertTrue(getHandler("/plugins/titan/sslcode", "GET") instanceof TitanKeySSLCodeGetHandler);
        Assert.assertTrue(getHandler("/plugins/titan/sslcode", "POST") instanceof TitanKeySSLCodeUpdateHandler);
        Assert.assertTrue(getHandler("/plugins/titan/whitelist/add", "POST") instanceof WhiteListAddHandler);
        Assert.assertTrue(getHandler("/plugins/titan/whitelist/delete", "POST") instanceof WhiteListDeleteHandler);
    }

    private void register(List<AdminHandler> handlers) {
        for (AdminHandler handler : handlers)
            dispatcher.register(handler);
    }

    private AdminHandler getHandler(String uri, String method) {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getRequestURI()).andReturn(uri).anyTimes();
        EasyMock.expect(request.getMethod()).andReturn(method).anyTimes();
        EasyMock.replay(request);
        return dispatcher.getHandler(request);
    }

}
