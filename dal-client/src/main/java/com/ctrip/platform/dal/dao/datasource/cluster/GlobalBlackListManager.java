package com.ctrip.platform.dal.dao.datasource.cluster;

public final class GlobalBlackListManager {

    private static volatile GlobalBlackListDepository blackListDepository = new GlobalBlackListDepository();

    public static GlobalBlackListDepository getBlackListDepository() {
        return blackListDepository;
    }

    private GlobalBlackListManager() {}

}
