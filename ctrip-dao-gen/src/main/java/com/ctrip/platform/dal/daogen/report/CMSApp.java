package com.ctrip.platform.dal.daogen.report;

public class CMSApp {
    private static final String[] NET_CONTAINER = new String[] {"windows_web_iis"};
    private static final String[] JAVA_CONTAINER = new String[] {"linux_tomcat", "linux_java"};

    private String appId;
    private String appName;
    private String chineseName;
    private String owner;
    private String ownerEmail;
    private String ownerCode;
    private String organizationName;
    private String appContainer;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getAppContainer() {
        return appContainer;
    }

    public void setAppContainer(String appContainer) {
        this.appContainer = appContainer;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public LangType getLangType() {
        if (appContainer == null || appContainer.isEmpty()) {
            return LangType.Others;
        }

        String temp = appContainer.trim().toLowerCase();
        for (String container : JAVA_CONTAINER) {
            if (temp.indexOf(container) > -1) {
                return LangType.Java;
            }
        }

        for (String container : NET_CONTAINER) {
            if (temp.indexOf(container) > -1) {
                return LangType.Net;
            }
        }

        return LangType.Others;
    }

}
