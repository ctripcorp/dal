package com.ctrip.framework.dal.dbconfig.plugin;

import qunar.tc.qconfig.plugin.PluginResult;
import qunar.tc.qconfig.plugin.WrappedRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author c7ch23en
 */
public abstract class AdminPluginAdapter extends PluginAdapter {

    public abstract PluginResult preHandle(HttpServletRequest request);

    public abstract PluginResult postHandle(HttpServletRequest request);

    @Override
    public PluginResult preHandle(WrappedRequest wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        return preHandle(request);
    }

    @Override
    public PluginResult postHandle(WrappedRequest wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        return postHandle(request);
    }

}
