package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.HashMap;
import java.util.Map;

public final class GlobalBlackListManager {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CAT_LOG_TYPE = "DAL.pickConnection";
    private static final String ADD_BLACK_LIST = "BlackList::addBlackList";

    private static volatile Map<HostSpec, Long> hostBlackList = new HashMap<>();
    private static volatile Map<HostSpec, Long> preBlackList = new HashMap<>();

    private GlobalBlackListManager(){}

    public static GlobalBlackListManager getInstance() {
        return new GlobalBlackListManager();
    }

    public static Map<HostSpec, Long> getHostBlackList() {
        return hostBlackList;
    }

    public static void addHostToBlackList(HostSpec host) {
        synchronized (GlobalBlackListManager.class) {
            LOGGER.logEvent(CAT_LOG_TYPE, ADD_BLACK_LIST, host.toString());
            Long currentTime = System.currentTimeMillis();
            hostBlackList.put(host, currentTime);
        }
    }

    public static Map<HostSpec, Long> getPreBlackList() {
        return preBlackList;
    }

    public static void addToPreBlackList(HostSpec host) {
        synchronized (GlobalBlackListManager.class) {
            Long currentTime = System.currentTimeMillis();
            preBlackList.put(host, currentTime);
        }
    }


}
