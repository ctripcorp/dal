package com.ctrip.framework.dal.dbconfig.plugin.dispatcher;

import com.ctrip.framework.dal.dbconfig.plugin.handler.PluginHandler;
import qunar.tc.qconfig.plugin.WrappedRequest;

/**
 * @author c7ch23en
 */
public interface HandlerDispatcher {

    void register(PluginHandler handler);

    PluginHandler getHandler(WrappedRequest wrappedRequest);

}
