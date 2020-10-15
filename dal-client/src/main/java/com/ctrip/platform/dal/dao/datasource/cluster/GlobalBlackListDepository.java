package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GlobalBlackListDepository {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CAT_LOG_TYPE = "DAL.pickConnection";
    private static final String ADD_BLACK_LIST = "BlackList::addBlackList";
    private static final String SCHEDULE_VALIDATE = "BlackList::scheduleValidate";

    private static volatile HashMap<HostSpec, Long> hostBlackList = new HashMap<>();
    private static volatile HashMap<HostSpec, Long> preBlackList = new HashMap<>();
    private static volatile ScheduledExecutorService scheduledExecutorService;
    private static final HashMap<HostSpec, Connection> longLiveConnection = new HashMap<>();
    private static final HashMap<HostSpec, Integer> clusterHostCount = new HashMap<>();
    private static final long initDelay = 10;
    private static final long period = 1000;

    static {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduleValidate();
    }

    private static void scheduleValidate() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                LOGGER.logEvent(CAT_LOG_TYPE, SCHEDULE_VALIDATE, new Date().toString());
                Set<HostSpec> keySet = preBlackList.keySet();
                for (HostSpec hostSpec : keySet) {

                }
            } catch (Throwable throwable) {

            }
        }, initDelay, period, TimeUnit.MILLISECONDS);
    }

    private static void validate(Connection connection, int hostCount) {
        try {
            connection.createStatement().execute("sql");
        } catch (CommunicationsException e) {

        } catch (SQLException e) {

        }
    }


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
