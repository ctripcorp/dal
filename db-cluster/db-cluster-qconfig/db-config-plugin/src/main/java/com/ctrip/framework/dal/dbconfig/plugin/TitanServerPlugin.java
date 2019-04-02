package com.ctrip.framework.dal.dbconfig.plugin;

import qunar.tc.qconfig.plugin.PluginRegisterPoint;
import qunar.tc.qconfig.plugin.PluginResult;
import qunar.tc.qconfig.plugin.WrappedRequest;

import java.util.List;

/**
 * @author c7ch23en
 */
public class TitanServerPlugin extends ServerPluginAdapter {

    @Override
    public void init() {}

    @Override
    public PluginResult preHandle(WrappedRequest wrappedRequest) {
        return null;
    }

    @Override
    public PluginResult postHandle(WrappedRequest wrappedRequest) {
        return null;
    }

    @Override
    public List<PluginRegisterPoint> registerPoints() {
        return null;
    }

}
