package com.ctrip.framework.dal.dbconfig.plugin.entity.titan;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lzyan on 2017/09/11.
 */
public class ConnectionCheckInputEntity {
    @SerializedName("dbtype")
    protected String dbType;    //mysql, sqlserver

    protected String env;       //pro, uat, lpt, fat

    protected String host;

    protected int port;

    protected String user;

    protected String password;

    @SerializedName("dbname")
    protected String dbName;

    //constructor
    public ConnectionCheckInputEntity() {
    }
    public ConnectionCheckInputEntity(String dbType, String env, String host, int port, String user, String password, String dbName) {
        this.dbType = dbType;
        this.env = env;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
    }





    //setter/getter
    public String getDbType() {
        return dbType;
    }
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getEnv() {
        return env;
    }
    public void setEnv(String env) {
        this.env = env;
    }

    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConnectionCheckInputEntity{");
        sb.append("dbType='").append(dbType).append('\'');
        sb.append(", env='").append(env).append('\'');
        sb.append(", host='").append(host).append('\'');
        sb.append(", port='").append(port).append('\'');
        sb.append(", user='").append(user).append('\'');
        //sb.append(", password='").append(password).append('\'');
        sb.append(", dbName='").append(dbName).append('\'');
        sb.append('}');
        return sb.toString();
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
        if (obj instanceof ConnectionCheckInputEntity) {
            ConnectionCheckInputEntity _o = (ConnectionCheckInputEntity) obj;

            if (!equals(dbType, _o.getDbType())) {
                return false;
            }
            if (!equals(env, _o.getEnv())) {
                return false;
            }
            if (!equals(host, _o.getHost())) {
                return false;
            }
            if (!equals(port, _o.getPort())) {
                return false;
            }
            if (!equals(user, _o.getUser())) {
                return false;
            }
            if (!equals(password, _o.getPassword())) {
                return false;
            }
            if (!equals(dbName, _o.getDbName())) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash * 31 + (dbType == null ? 0 : dbType.hashCode());
        hash = hash * 31 + (env == null ? 0 : env.hashCode());
        hash = hash * 31 + (host == null ? 0 : host.hashCode());
        hash = hash * 31 + (port);
        hash = hash * 31 + (user == null ? 0 : user.hashCode());
        hash = hash * 31 + (password == null ? 0 : password.hashCode());
        hash = hash * 31 + (dbName == null ? 0 : dbName.hashCode());
        return hash;
    }



}
