package com.ctrip.framework.dal.dbconfig.plugin.entity.titan;


import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.google.common.base.Strings;


/**
 * Created by lzyan on 2017/8/25.
 */
public class SiteOutputEntity {
    //field
    private Integer id;    //[2017-10-12]
    private String name;     //keyName
    private String subEnv;
    private Boolean enabled = Boolean.valueOf(true);
    private String connectionString;
    private String sslCode;
    private String providerName;
    private Integer timeOut = Integer.valueOf(0);
    private String createUser;
    private String updateUser;
    private String whiteList;
    private String blackList;
    private String permissions;
    private String freeVerifyIpList;
    private String freeVerifyAppIdList;
    private String mhaLastUpdateTime;
    private ConnectionInfo connectionInfo;


    //constructor
    public SiteOutputEntity(){}
    public SiteOutputEntity(Integer id, String name, String subEnv, Boolean enabled, String connectionString, String sslCode,
                            String providerName, Integer timeOut, String createUser, String updateUser,
                            String whiteList, String blackList, String permissions, String freeVerifyIpList,
                            String freeVerifyAppIdList, String mhaLastUpdateTime, ConnectionInfo connectionInfo ){
        this.connectionInfo = connectionInfo;
        this.id = id;
        this.name = name;
        this.subEnv = subEnv;
        this.enabled = enabled;
        this.sslCode = sslCode;
        this.providerName = providerName;
        this.timeOut = timeOut;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.whiteList = whiteList;
        this.blackList = blackList;
        this.permissions = permissions;
        this.freeVerifyIpList = freeVerifyIpList;
        this.freeVerifyAppIdList = freeVerifyAppIdList;
        this.mhaLastUpdateTime = mhaLastUpdateTime;
        this.connectionString = buildConnectionString();
    }

    //setter/getter
    public Integer getId() {
        return id;
    }
    public SiteOutputEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }
    public SiteOutputEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getSubEnv() {
        return subEnv;
    }
    public SiteOutputEntity setSubEnv(String subEnv) {
        this.subEnv = subEnv;
        return this;
    }

    public Boolean isEnabled() {
        return enabled;
    }
    public SiteOutputEntity setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getConnectionString() {
        return connectionString;
    }
    public SiteOutputEntity setConnectionString(String connectionString) {
        this.connectionString = connectionString;
        return this;
    }

    public String getSslCode() {
        return sslCode;
    }
    public SiteOutputEntity setSslCode(String sslCode) {
        this.sslCode = sslCode;
        return this;
    }

    public String getProviderName() {
        return providerName;
    }
    public SiteOutputEntity setProviderName(String providerName) {
        this.providerName = providerName;
        return this;
    }

    public Integer getTimeOut() {
        return timeOut;
    }
    public SiteOutputEntity setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public String getCreateUser() {
        return createUser;
    }
    public SiteOutputEntity setCreateUser(String createUser) {
        this.createUser = createUser;
        return this;
    }

    public String getUpdateUser() {
        return updateUser;
    }
    public SiteOutputEntity setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
        return this;
    }

    public String getWhiteList() {
        return whiteList;
    }
    public SiteOutputEntity setWhiteList(String whiteList) {
        this.whiteList = whiteList;
        return this;
    }

    public String getBlackList() {
        return blackList;
    }
    public SiteOutputEntity setBlackList(String blackList) {
        this.blackList = blackList;
        return this;
    }

    public String getPermissions() {
        return permissions;
    }
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getFreeVerifyIpList() {
        return freeVerifyIpList;
    }
    public void setFreeVerifyIpList(String freeVerifyIpList) {
        this.freeVerifyIpList = freeVerifyIpList;
    }

    public String getFreeVerifyAppIdList() {
        return freeVerifyAppIdList;
    }
    public void setFreeVerifyAppIdList(String freeVerifyAppIdList) {
        this.freeVerifyAppIdList = freeVerifyAppIdList;
    }

    public String getMhaLastUpdateTime() {
        return mhaLastUpdateTime;
    }
    public void setMhaLastUpdateTime(String mhaLastUpdateTime) {
        this.mhaLastUpdateTime = mhaLastUpdateTime;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }
    public SiteOutputEntity setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
        return this;
    }


    //--- buz method ---
    //build connection string. Here connection string is only for titan service, so host will use serverName.
    public String buildConnectionString() {
        String connString = null;
        switch (providerName) {
            case TitanConstants.NAME_MYSQL_PROVIDER:
                connString = String.format(TitanConstants.FORMAT_MYSQL_CONNECTIONSTRING,
                        connectionInfo.getServer(),
                        connectionInfo.getPort(),
                        connectionInfo.getUid(),
                        connectionInfo.getPassword(),
                        connectionInfo.getDbName());
                break;
            case TitanConstants.NAME_SQLSERVER_PROVIDER:
                connString = String.format(TitanConstants.FORMAT_SQLSERVER_CONNECTIONSTRING,
                        connectionInfo.getServer(),
                        connectionInfo.getPort(),
                        connectionInfo.getUid(),
                        connectionInfo.getPassword(),
                        connectionInfo.getDbName());
                break;
            default:
                break;
        }
        if(!Strings.isNullOrEmpty(connectionInfo.getExtParam()) && connString != null) {
            connString = connString + connectionInfo.getExtParam();
        }
        return connString;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SiteOutputEntity{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", subEnv='").append(subEnv).append('\'');
        sb.append(", connectionString='").append(connectionString).append('\'');
        sb.append(", providerName='").append(providerName).append('\'');
        sb.append(", timeOut='").append(timeOut).append('\'');
        sb.append(", sslCode='").append(sslCode).append('\'');
        sb.append(", enabled='").append(enabled).append('\'');
        sb.append(", createUser='").append(createUser).append('\'');
        sb.append(", updateUser='").append(updateUser).append('\'');
        sb.append(", whiteList='").append(whiteList).append('\'');
        sb.append(", blackList='").append(blackList).append('\'');
        sb.append(", permissions='").append(permissions).append('\'');
        sb.append(", freeVerifyIpList='").append(freeVerifyIpList).append('\'');
        sb.append(", freeVerifyAppIdList='").append(freeVerifyAppIdList).append('\'');
        sb.append(", mhaLastUpdateTime='").append(mhaLastUpdateTime).append('\'');
        sb.append(", connectionInfo=").append(connectionInfo);
        sb.append('}');
        return sb.toString();
    }


}
