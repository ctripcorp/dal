package com.ctrip.framework.dal.dbconfig.plugin.handler;

import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import javax.servlet.http.HttpServletRequest;

/**
 * @author c7ch23en
 */
public class AdminHandlerDispatcher {

    private Table<String, String, AdminHandler> handlers = HashBasedTable.create();

    public void register(AdminHandler handler) {
        if (handler == null)
            return;
        String uri = handler.getUri();
        String method = handler.getMethod();
        if (handlers.put(uri, method, handler) != null)
            throw new DbConfigPluginException("AdminHandler registration conflicts");
    }

    public AdminHandler getHandler(HttpServletRequest request) {
        if (request == null)
            return null;
        String uri = request.getRequestURI();
        String method = request.getMethod();
        return handlers.get(uri, method);
    }

}
