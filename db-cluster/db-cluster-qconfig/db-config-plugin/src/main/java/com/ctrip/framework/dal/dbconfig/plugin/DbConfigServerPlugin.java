package com.ctrip.framework.dal.dbconfig.plugin;

import qunar.tc.qconfig.plugin.*;

import java.util.List;

/**
 * @author c7ch23en
 */
public class DbConfigServerPlugin implements Plugin {

    @Override
    public List<PluginRegisterPoint> registerPoints() {
        return null;
    }

    @Override
    public void init(QconfigService qconfigService) {

    }

    @Override
    public PluginResult preHandle(WrappedRequest wrappedRequest) {
        return null;
    }

    @Override
    public PluginResult postHandle(WrappedRequest wrappedRequest) {
        return null;
    }

}
