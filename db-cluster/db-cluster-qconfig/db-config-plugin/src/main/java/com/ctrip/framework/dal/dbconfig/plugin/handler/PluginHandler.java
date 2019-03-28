package com.ctrip.framework.dal.dbconfig.plugin.handler;

import qunar.tc.qconfig.plugin.PluginResult;
import qunar.tc.qconfig.plugin.WrappedRequest;

/**
 * @author c7ch23en
 */
public interface PluginHandler {

    boolean checkPermission(WrappedRequest wrappedRequest);

    PluginResult execute(WrappedRequest wrappedRequest);

}
