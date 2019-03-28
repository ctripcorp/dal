package com.ctrip.framework.dal.dbconfig.plugin.dispatcher;

import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.handler.PluginHandler;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import javax.servlet.http.HttpServletRequest;

/**
 * @author c7ch23en
 */
public class AdminHandlerDispatcher implements HandlerDispatcher {

    private Table<String, String, PluginHandler> handlers = HashBasedTable.create();

    @Override
    public void register(PluginHandler handler) {
        if (handler == null)
            return;
        if (handler instanceof AdminHandler) {
            AdminHandler adminHandler = (AdminHandler) handler;
            String uri = adminHandler.getUri();
            String method = adminHandler.getMethod();
            if (handlers.put(uri, method, handler) != null)
                throw new DbConfigPluginException("AdminHandler registration conflicts");
        }
        throw new UnsupportedOperationException("AdminHandlerDispatcher supports only AdminHandler registration");
    }

    @Override
    public PluginHandler getHandler(HttpServletRequest request) {
        if (request == null)
            return null;
        String uri = request.getRequestURI();
        String method = request.getMethod();
        return handlers.get(uri, method);
    }

}
