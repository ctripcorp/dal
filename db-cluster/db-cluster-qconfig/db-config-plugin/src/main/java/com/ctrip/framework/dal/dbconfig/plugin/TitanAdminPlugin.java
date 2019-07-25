package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandlerDispatcher;
import com.ctrip.framework.dal.dbconfig.plugin.handler.titan.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import qunar.tc.qconfig.plugin.PluginRegisterPoint;
import qunar.tc.qconfig.plugin.PluginResult;
import qunar.tc.qconfig.plugin.PluginStatusCode;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author c7ch23en
 */
public class TitanAdminPlugin extends AdminPluginAdapter {

    private AdminHandlerDispatcher dispatcher;

    @Override
    public void init() {
        dispatcher = new AdminHandlerDispatcher();
        PluginConfigManager pluginConfigManager = getPluginConfigManager();
        List<AdminHandler> adminHandlers = Lists.newArrayList();
        adminHandlers.add(new TitanKeyPostHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new TitanKeyForceDataWashHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new TitanKeyGetHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new TitanKeyListByTimeHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new TitanKeyListHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new TitanKeyMHAUpdateHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new TitanKeySSLCodeGetHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new TitanKeySSLCodeUpdateHandler(getQconfigService(), pluginConfigManager));

        adminHandlers.add(new DbNameListHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new KeyListByDbNameHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new WhiteListAddHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new WhiteListDeleteHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new IndexBuildHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new TitanKeyPermissionMergeHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new FreeVerifyAddHandler(getQconfigService(), pluginConfigManager));
        adminHandlers.add(new FreeVerifyDeleteHandler(getQconfigService(), pluginConfigManager));


        for (AdminHandler handler : adminHandlers) {
            dispatcher.register(handler);
        }
    }

    @Override
    public PluginResult preHandle(HttpServletRequest request) {
        PluginResult pluginResult = null;
        try {
            AdminHandler handler = dispatcher.getHandler(request);
            if (handler != null) {
                pluginResult = handler.preHandle(request);
            } else {
                String requestUri = request.getRequestURI();
                pluginResult = new PluginResult(PluginStatusCode.TITAN_ILLEGAL_REQUEST, "preHandle(): not find process handler, requestUri=" + requestUri);
            }
        } catch (Exception e) {
            pluginResult = new PluginResult(PluginStatusCode.TITAN_NOT_DEFINED, "preHandle(): handler error. " + e.getMessage());
        }
        return pluginResult;
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = null;
        try {
            AdminHandler handler = dispatcher.getHandler(request);
            if (handler != null) {
                pluginResult = handler.postHandle(request);
            } else {
                String requestUri = request.getRequestURI();
                pluginResult = new PluginResult(PluginStatusCode.TITAN_ILLEGAL_REQUEST, "postHandle(): not find process handler, requestUri=" + requestUri);
            }
        } catch (Exception e) {
            pluginResult = new PluginResult(PluginStatusCode.TITAN_NOT_DEFINED, "postHandle(): handler error. " + e.getMessage());
        }
        return pluginResult;
    }

    @Override
    public List<PluginRegisterPoint> registerPoints() {
        List<PluginRegisterPoint> registerPointList = new ImmutableList.Builder<PluginRegisterPoint>()
                .add(PluginRegisterPoint.ADM_TITAN_GET)
                .add(PluginRegisterPoint.ADM_TITAN_POST)
                .add(PluginRegisterPoint.ADM_TITAN_PUT)
                .add(PluginRegisterPoint.ADM_TITAN_DELETE)
                .build();
        return registerPointList;
    }

}
