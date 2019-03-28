package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.context.RequestContext;
import com.ctrip.framework.dal.dbconfig.plugin.context.ResultContext;
import com.ctrip.framework.dal.dbconfig.plugin.proxy.ServerPluginProxy;
import qunar.tc.qconfig.plugin.PluginRegisterPoint;

import java.util.List;

/**
 * @author c7ch23en
 */
public class TitanServerPlugin extends ServerPluginProxy {

    @Override
    public void init() {
    }

    @Override
    public ResultContext preHandle(RequestContext requestCtx) {
        return null;
    }

    @Override
    public ResultContext postHandle(RequestContext requestCtx) {
        return null;
    }

    @Override
    public List<PluginRegisterPoint> registerPoints() {
        return null;
    }

}
