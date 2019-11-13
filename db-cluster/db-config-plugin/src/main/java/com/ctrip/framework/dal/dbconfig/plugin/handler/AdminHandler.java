package com.ctrip.framework.dal.dbconfig.plugin.handler;

import qunar.tc.qconfig.plugin.PluginResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @author c7ch23en
 */
public interface AdminHandler extends PluginHandler {

    String getUri();

    String getMethod();

    PluginResult preHandle(HttpServletRequest request);

    PluginResult postHandle(HttpServletRequest request);

}
