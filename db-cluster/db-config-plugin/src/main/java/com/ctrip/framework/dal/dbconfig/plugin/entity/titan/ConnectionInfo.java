package com.ctrip.framework.dal.dbconfig.plugin.entity.titan;

/**
 * Created by lzyan on 2017/8/25.
 */
public class ConnectionInfo {
    protected String server;
    protected String serverIp;  //[2017-10-30] new add
    protected String port;
    protected String uid;
    protected String password;  // = "*****"
    protected String dbName;
    protected String extParam;

    //constructor
    public ConnectionInfo() {
    }


    protected boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else if (o2 == null) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConnectionInfo) {
            ConnectionInfo _o = (ConnectionInfo) obj;
            if (!equals(server, _o.getServer())) {
                return false;
            }
            if (!equals(serverIp, _o.getServerIp())) {
                return false;
            }
            if (!equals(port, _o.getPort())) {
                return false;
            }
            if (!equals(uid, _o.getUid())) {
                return false;
            }
            if (!equals(password, _o.getPassword())) {
                return false;
            }
            if (!equals(dbName, _o.getDbName())) {
                return false;
            }
            if (!equals(extParam, _o.getExtParam())) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash * 31 + (server == null ? 0 : server.hashCode());
        hash = hash * 31 + (serverIp == null ? 0 : serverIp.hashCode());
        hash = hash * 31 + (port == null ? 0 : port.hashCode());
        hash = hash * 31 + (uid == null ? 0 : uid.hashCode());
        hash = hash * 31 + (password == null ? 0 : password.hashCode());
        hash = hash * 31 + (dbName == null ? 0 : dbName.hashCode());
        hash = hash * 31 + (extParam == null ? 0 : extParam.hashCode());
        return hash;
    }

//    public boolean isUpdatePwd() {
//        return updatePwd != null && updatePwd.booleanValue();
//    }

//    @Override
//    public void mergeAttributes(ConnectionInfo other) {
//    }


    //setter/getter
    public String getServer() {
        return server;
    }
    public ConnectionInfo setServer(String server) {
        this.server = server;
        return this;
    }

    public String getServerIp() {
        return serverIp;
    }
    public ConnectionInfo setServerIp(String serverIp) {
        this.serverIp = serverIp;
        return this;
    }

    public String getPort() {
        return port;
    }
    public ConnectionInfo setPort(String port) {
        this.port = port;
        return this;
    }

    public String getUid() {
        return uid;
    }
    public ConnectionInfo setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getPassword() {
        return password;
    }
    public ConnectionInfo setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDbName() {
        return dbName;
    }
    public ConnectionInfo setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public String getExtParam() {
        return extParam;
    }
    public ConnectionInfo setExtParam(String extParam) {
        this.extParam = extParam;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConnectionInfo{");
        sb.append("server='").append(server).append('\'');
        sb.append(", serverIp='").append(serverIp).append('\'');
        sb.append(", port='").append(port).append('\'');
        sb.append(", uid='").append(uid).append('\'');
        //sb.append(", password='").append(password).append('\'');    //TODO: comment here after debug over
        sb.append(", dbName='").append(dbName).append('\'');
        sb.append(", extParam='").append(extParam).append('\'');
        sb.append('}');
        return sb.toString();
    }


}
