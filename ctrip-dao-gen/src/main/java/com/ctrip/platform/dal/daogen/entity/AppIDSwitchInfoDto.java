package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/11.
 */
public class AppIDSwitchInfoDto {
    private String appID;

    private List<DalClientSwitchInfoDto> ClientList;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public List<DalClientSwitchInfoDto> getClientList() {
        return ClientList;
    }

    public void setClientList(List<DalClientSwitchInfoDto> clientList) {
        ClientList = clientList;
    }
}
