package com.ctrip.framework.dal.dbconfig.plugin.entity.titan;


public class MhaInputBasicData {
    //field
    private String keyname;
    private String server;
    private int port;

    public MhaInputBasicData() {
    }
    public MhaInputBasicData(String keyname, String server, int port) {
        this.keyname = keyname;
        this.server = server;
        this.port = port;
    }

    //getter/setter
    public String getKeyname() {
        return keyname;
    }
    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MhaInputBasicData{");
        sb.append("keyname='").append(keyname).append('\'');
        sb.append(", server='").append(server).append('\'');
        sb.append(", port=").append(port);
        sb.append('}');
        return sb.toString();
    }

}
