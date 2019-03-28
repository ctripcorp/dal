package com.ctrip.framework.dal.dbconfig.plugin.proxy;

import com.ctrip.framework.dal.dbconfig.plugin.context.ResultContext;
import qunar.tc.qconfig.plugin.PluginResult;
import qunar.tc.qconfig.plugin.WrappedRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author c7ch23en
 */
public abstract class AdminPluginProxy extends PluginProxy {

    public abstract ResultContext preHandle(HttpServletRequest request);

    @Override
    public PluginResult preHandle(WrappedRequest wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        ResultContext resultCtx = preHandle(request);
        return translateResult(resultCtx);
    }

}
