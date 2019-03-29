package com.ctrip.framework.dal.dbconfig.plugin.config;

import com.ctrip.framework.dal.dbconfig.plugin.context.IEnvProfile;
import qunar.tc.qconfig.plugin.QconfigService;

/**
 * @author c7ch23en
 */
public class QServiceConfigProvider implements ConfigProvider {

    private QconfigService qconfigService;
    private String groupId;
    private String fileName;
    private IEnvProfile envProfile;

    public QServiceConfigProvider(QconfigService qconfigService, String groupId, String fileName, IEnvProfile envProfile) {
        this.qconfigService = qconfigService;
        this.groupId = groupId;
        this.fileName = fileName;
        this.envProfile = envProfile;
    }

    @Override
    public String getStringValue(String key) {
        // TODO: to be implemented
        return null;
    }

}
