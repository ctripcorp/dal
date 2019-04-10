package com.ctrip.framework.dal.dbconfig.plugin;

import qunar.tc.qconfig.plugin.Plugin;
import qunar.tc.qconfig.plugin.QconfigService;

/**
 * @author c7ch23en
 */
public abstract class PluginAdapter implements Plugin {

    private QconfigService qconfigService;

    protected QconfigService getQconfigService() {
        return qconfigService;
    }

    protected void setQconfigService(QconfigService qconfigService) {
        this.qconfigService = qconfigService;
    }

    @Override
    public void init(QconfigService qconfigService) {
        setQconfigService(qconfigService);
        init();
    }

    public abstract void init();

}
