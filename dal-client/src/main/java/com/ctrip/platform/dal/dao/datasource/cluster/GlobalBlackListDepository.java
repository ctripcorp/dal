package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.HashMap;
import java.util.Map;

public class GlobalBlackListDepository {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CAT_LOG_TYPE = "DAL.pickConnection";
    private static final String ADD_BLACK_LIST = "BlackList::addBlackList";

    private static volatile HashMap<HostSpec, Long> hostBlackList = new HashMap<>();
    private static volatile HashMap<HostSpec, Long> preBlackList = new HashMap<>();

    public HashMap<HostSpec, Long> getHostBlackList() {
        HashMap<HostSpec, Long> cloneHostBlackList = new HashMap<>(hostBlackList.size());

        for (HostSpec hostSpec : hostBlackList.keySet()) {
            cloneHostBlackList.put(hostSpec.clone(), hostBlackList.get(hostSpec));
        }
        return cloneHostBlackList;
    }

    public void addHostToBlackList(HostSpec host) {
        synchronized (GlobalBlackListManager.class) {
            LOGGER.logEvent(CAT_LOG_TYPE, ADD_BLACK_LIST, host.toString());
            Long currentTime = System.currentTimeMillis();
            hostBlackList.put(host, currentTime);
        }
    }

    public HashMap<HostSpec, Long> getPreBlackList() {
        HashMap<HostSpec, Long> clonePreBlackList = new HashMap<>(preBlackList);

        for (HostSpec hostSpec : preBlackList.keySet()) {
            clonePreBlackList.put(hostSpec.clone(), preBlackList.get(hostSpec));
        }
        return clonePreBlackList;
    }

    public void addToPreBlackList(HostSpec host) {
        synchronized (GlobalBlackListManager.class) {
            Long currentTime = System.currentTimeMillis();
            preBlackList.put(host, currentTime);
        }
    }
}
