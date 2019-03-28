package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.context.RequestContext;
import com.ctrip.framework.dal.dbconfig.plugin.context.ResultContext;
import com.ctrip.framework.dal.dbconfig.plugin.dispatcher.AdminHandlerDispatcher;
import com.ctrip.framework.dal.dbconfig.plugin.dispatcher.HandlerDispatcher;
import com.ctrip.framework.dal.dbconfig.plugin.handler.PluginHandler;
import com.ctrip.framework.dal.dbconfig.plugin.proxy.AdminPluginProxy;
import qunar.tc.qconfig.plugin.PluginRegisterPoint;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author c7ch23en
 */
public class TitanAdminPlugin extends AdminPluginProxy {

    private HandlerDispatcher handlerDispatcher = new AdminHandlerDispatcher();

    @Override
    public void init() {
    }

    @Override
    public ResultContext preHandle(HttpServletRequest request) {
        PluginHandler handler = handlerDispatcher.getHandler(request);
        return null;
    }

    @Override
    public ResultContext postHandle(RequestContext requestCtx) {
        return null;
    }

    @Override
    public List<PluginRegisterPoint> registerPoints() {
        return null;
    }

}
