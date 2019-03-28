package com.ctrip.framework.dal.dbconfig.plugin.dispatcher;

import com.ctrip.framework.dal.dbconfig.plugin.handler.PluginHandler;
import qunar.tc.qconfig.plugin.WrappedRequest;

/**
 * @author c7ch23en
 */
public class ServerHandlerDispatcher implements HandlerDispatcher {

    @Override
    public void register(PluginHandler handler) {
    }

    @Override
    public PluginHandler getHandler(WrappedRequest wrappedRequest) {
        return null;
    }

}
