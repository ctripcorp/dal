package com.ctrip.framework.dal.dbconfig.plugin.proxy;

import com.ctrip.framework.dal.dbconfig.plugin.context.RequestContext;
import com.ctrip.framework.dal.dbconfig.plugin.context.ResultContext;
import qunar.tc.qconfig.plugin.PluginResult;
import qunar.tc.qconfig.plugin.WrappedRequest;

/**
 * @author c7ch23en
 */
public abstract class ServerPluginProxy extends PluginProxy {

    public abstract ResultContext preHandle(RequestContext requestCtx);

    @Override
    public PluginResult preHandle(WrappedRequest wrappedRequest) {
        RequestContext requestCtx = convertRequest(wrappedRequest);
        ResultContext resultCtx = preHandle(requestCtx);
        return translateResult(resultCtx);
    }

}
