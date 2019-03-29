package com.ctrip.framework.dal.dbconfig.plugin.config;

import com.ctrip.framework.dal.dbconfig.plugin.context.IEnvProfile;
import qunar.tc.qconfig.plugin.QconfigService;

/**
 * @author c7ch23en
 */
public class QEnvConfigProvider implements EnvConfigProvider {

    private QconfigService qconfigService;
    private String groupId;
    private String fileName;

    public QEnvConfigProvider(QconfigService qconfigService, String groupId, String fileName) {
        this.qconfigService = qconfigService;
        this.groupId = groupId;
        this.fileName = fileName;
    }

    @Override
    public String getStringValue(String key, IEnvProfile envProfile) {
        // TODO: to be implemented
        return null;
    }

}
