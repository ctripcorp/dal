package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandlerDispatcher;
import qunar.tc.qconfig.plugin.PluginRegisterPoint;
import qunar.tc.qconfig.plugin.PluginResult;

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
    }

    @Override
    public PluginResult preHandle(HttpServletRequest request) {
        AdminHandler handler = dispatcher.getHandler(request);
        return null;
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        return null;
    }

    @Override
    public List<PluginRegisterPoint> registerPoints() {
        return null;
    }

}
