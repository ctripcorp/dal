package com.ctrip.framework.dal.dbconfig.plugin.proxy;

import com.ctrip.framework.dal.dbconfig.plugin.context.RequestContext;
import com.ctrip.framework.dal.dbconfig.plugin.context.ResultContext;
import qunar.tc.qconfig.plugin.Plugin;
import qunar.tc.qconfig.plugin.PluginResult;
import qunar.tc.qconfig.plugin.QconfigService;
import qunar.tc.qconfig.plugin.WrappedRequest;

/**
 * @author c7ch23en
 */
public abstract class PluginProxy implements Plugin {

    private QconfigService qconfigService;

    protected QconfigService getQconfigService() {
        return qconfigService;
    }

    private void setQconfigService(QconfigService qconfigService) {
        this.qconfigService = qconfigService;
    }

    @Override
    public void init(QconfigService qconfigService) {
        setQconfigService(qconfigService);
        init();
    }

    public abstract void init();

    @Override
    public PluginResult postHandle(WrappedRequest wrappedRequest) {
        RequestContext requestCtx = convertRequest(wrappedRequest);
        ResultContext resultCtx = postHandle(requestCtx);
        return translateResult(resultCtx);
    }

    public abstract ResultContext postHandle(RequestContext requestCtx);

    protected RequestContext convertRequest(WrappedRequest wrappedRequest) {
        return null;
    }

    protected PluginResult translateResult(ResultContext resultCtx) {
        return null;
    }

}
