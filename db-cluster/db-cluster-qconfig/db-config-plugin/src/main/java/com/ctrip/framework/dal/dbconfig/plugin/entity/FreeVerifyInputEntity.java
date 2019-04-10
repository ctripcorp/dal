package com.ctrip.framework.dal.dbconfig.plugin.entity;

/**
 * Created by lzyan on 2018/10/10.
 */
public class FreeVerifyInputEntity {
    private String titanKeyList;
    private String freeVerifyIpList;
    private String freeVerifyAppIdList;


    //constructor
    public FreeVerifyInputEntity(){}
    public FreeVerifyInputEntity(String titanKeyList, String freeVerifyIpList, String freeVerifyAppIdList){
        this.titanKeyList = titanKeyList;
        this.freeVerifyIpList = freeVerifyIpList;
        this.freeVerifyAppIdList = freeVerifyAppIdList;
    }

    //setter/getter
    public String getTitanKeyList() {
        return titanKeyList;
    }
    public void setTitanKeyList(String titanKeyList) {
        this.titanKeyList = titanKeyList;
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

    //--- buz method ---


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FreeVerifyInputEntity{");
        sb.append("titanKeyList='").append(titanKeyList).append('\'');
        sb.append(", freeVerifyIpList='").append(freeVerifyIpList).append('\'');
        sb.append(", freeVerifyAppIdList='").append(freeVerifyAppIdList).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
