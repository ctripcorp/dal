package com.ctrip.framework.dal.dbconfig.plugin.entity.titan;

/**
 * Created by lzyan on 2017/8/25.
 */
public class SiteInputEntity {
    //field
    private Integer id;    //[2017-10-12] optional field, only for the old key imported from db
    private String keyName;
    private String providerName;
    private String serverName;
    private String serverIp;    //[2017-10-26] new added
    private String port;
    private String uid;
    private String password;    //new added
    private String dbName;
    private String extParam;
    private Integer timeOut = 15;
    private String sslCode;
    private Boolean enabled = true;
    private String createUser;
    private String updateUser;
    private String whiteList;
    private String blackList;
    private String permissions;
    private String freeVerifyIpList;
    private String freeVerifyAppIdList;
    private String mhaLastUpdateTime;



    //constructor
    public SiteInputEntity(){}
    public SiteInputEntity(Integer id, String keyName, String providerName, String serverName, String serverIp,
                           String port, String uid, String password, String dbName,
                           String extParam, Integer timeOut, String sslCode, Boolean enabled,
                           String createUser, String updateUser, String whiteList, String blackList,
                           String permissions, String freeVerifyIpList, String freeVerifyAppIdList,
                           String mhaLastUpdateTime
                           ){
        this.id = id;
        this.keyName = keyName;
        this.providerName = providerName;
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.port = port;
        this.uid = uid;
        this.password = password;
        this.dbName = dbName;
        this.extParam = extParam;
        this.timeOut = timeOut;
        this.sslCode = sslCode;
        this.enabled = enabled;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.whiteList = whiteList;
        this.blackList = blackList;
        this.permissions = permissions;
        this.freeVerifyIpList = freeVerifyIpList;
        this.freeVerifyAppIdList = freeVerifyAppIdList;
        this.mhaLastUpdateTime = mhaLastUpdateTime;
    }

    //setter/getter
    public Integer getId() {
        return id;
    }
    public SiteInputEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getKeyName() {
        return keyName;
    }
    public SiteInputEntity setKeyName(String keyName) {
        this.keyName = keyName;
        return this;
    }

    public String getProviderName() {
        return providerName;
    }
    public SiteInputEntity setProviderName(String providerName) {
        this.providerName = providerName;
        return this;
    }

    public String getServerName() {
        return serverName;
    }
    public SiteInputEntity setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getServerIp() {
        return serverIp;
    }
    public SiteInputEntity setServerIp(String serverIp) {
        this.serverIp = serverIp;
        return this;
    }

    public String getPort() {
        return port;
    }
    public SiteInputEntity setPort(String port) {
        this.port = port;
        return this;
    }

    public String getUid() {
        return uid;
    }
    public SiteInputEntity setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getPassword() {
        return password;
    }
    public SiteInputEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDbName() {
        return dbName;
    }
    public SiteInputEntity setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public String getExtParam() {
        return extParam;
    }
    public SiteInputEntity setExtParam(String extParam) {
        this.extParam = extParam;
        return this;
    }

    public Integer getTimeOut() {
        return timeOut;
    }
    public SiteInputEntity setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public String getSslCode() {
        return sslCode;
    }
    public SiteInputEntity setSslCode(String sslCode) {
        this.sslCode = sslCode;
        return this;
    }

    public Boolean isEnabled() {
        return enabled;
    }
    public SiteInputEntity setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getCreateUser() {
        return createUser;
    }
    public SiteInputEntity setCreateUser(String createUser) {
        this.createUser = createUser;
        return this;
    }

    public String getUpdateUser() {
        return updateUser;
    }
    public SiteInputEntity setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
        return this;
    }

    public String getWhiteList() {
        return whiteList;
    }
    public SiteInputEntity setWhiteList(String whiteList) {
        this.whiteList = whiteList;
        return this;
    }

    public String getBlackList() {
        return blackList;
    }
    public SiteInputEntity setBlackList(String blackList) {
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

//--- buz method ---


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SiteInputEntity{");
        sb.append("id='").append(id).append('\'');
        sb.append(", keyName='").append(keyName).append('\'');
        sb.append(", providerName='").append(providerName).append('\'');
        sb.append(", serverName='").append(serverName).append('\'');
        sb.append(", serverIp='").append(serverIp).append('\'');
        sb.append(", port='").append(port).append('\'');
        sb.append(", uid='").append(uid).append('\'');
        //sb.append(", password='").append(password).append('\'');  //hide 'password'
        sb.append(", dbName='").append(dbName).append('\'');
        sb.append(", extParam='").append(extParam).append('\'');
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
        sb.append('}');
        return sb.toString();
    }
}
