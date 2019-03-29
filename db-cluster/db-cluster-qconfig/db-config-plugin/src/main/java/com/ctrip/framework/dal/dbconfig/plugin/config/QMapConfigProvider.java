package com.ctrip.framework.dal.dbconfig.plugin.config;

/**
 * @author c7ch23en
 */
public class QMapConfigProvider implements ConfigProvider {

    private String groupId;
    private String fileName;

    public QMapConfigProvider(String groupId, String fileName) {
        this.groupId = groupId;
        this.fileName = fileName;
    }

    @Override
    public String getStringValue(String key) {
        // TODO: to be implemented
        return null;
    }

}
