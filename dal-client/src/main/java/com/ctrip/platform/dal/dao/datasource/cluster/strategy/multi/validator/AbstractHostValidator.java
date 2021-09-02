package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Author limingdong
 * @create 2021/8/19
 */
public abstract class AbstractHostValidator implements HostValidator {

    protected static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String ADD_BLACK_LIST = "Validator::addToBlackList";
    private static final String ADD_PRE_BLACK_LIST = "Validator::addToPreBlackList";
    private static final String REMOVE_BLACK_LIST = "Validator::removeFromBlackList";
    private static final String REMOVE_PRE_BLACK_LIST = "Validator::removeFromPreBlackList";
    protected static final String ASYNC_VALIDATE_RESULT = "Validator::asyncValidateResult:";
    protected String CAT_LOG_TYPE;

    protected volatile Set<HostSpec> configuredHosts;
    protected volatile List<HostSpec> orderHosts;
    protected volatile ConnectionFactory connectionFactory;
    private volatile long failOverTime;
    private volatile long blackListTimeOut;
    private volatile long fixedValidatePeriod = 30000;
    private volatile RouteStrategyStatus status; // birth --> init --> destroy
    private volatile HashMap<HostSpec, Long> lastValidateMap = new HashMap<>();
    private volatile ScheduledExecutorService fixedPeriodValidateService;
    private volatile ScheduledExecutorService fixed1sValidateService = Executors.newSingleThreadScheduledExecutor();
    private static volatile ExecutorService asyncService = Executors.newFixedThreadPool(4);
    private static volatile ConcurrentHashMap<HostSpec, Long> hostBlackList = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<HostSpec, Long> preBlackList = new ConcurrentHashMap<>();
    protected Long ONE_SECOND = 900L; // 100ms threshold to tolerant schedule time fault

    private enum RouteStrategyStatus {
        birth, init, destroy
    }

    public AbstractHostValidator() {
        fixedScheduleStart();
        status = RouteStrategyStatus.birth;
    }

    public AbstractHostValidator(Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        this();
        this.configuredHosts = configuredHosts;
        this.failOverTime = failOverTime;
        this.blackListTimeOut = blackListTimeOut;
        this.orderHosts = orderHosts;
        this.fixedValidatePeriod = fixedValidatePeriod;
        CAT_LOG_TYPE = getCatLogType();
        init();
    }

    private void init() {
        initLastValidateMap();
        status = RouteStrategyStatus.init;
    }

    private void initLastValidateMap() {
        for (HostSpec host : orderHosts) {
            lastValidateMap.put(host, System.currentTimeMillis());
        }
    }

    private void fixedScheduleStart() {
        if (startFixedScheduler()) {
            try {
                fixedPeriodValidateService = Executors.newSingleThreadScheduledExecutor();
                fixedPeriodValidateService.scheduleAtFixedRate(() -> asyncValidate(orderHosts), 1000, fixedValidatePeriod, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LOGGER.warn("start schedule1 error", e);
            }
        }

        try {
            fixed1sValidateService.scheduleAtFixedRate(() -> asyncValidate(getAllBlackListNodes()), 1000, 1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.warn("start schedule2 error", e);
        }
    }

    protected ArrayList<HostSpec> getAllBlackListNodes() {
        Set<HostSpec> keySet = new HashSet<>(preBlackList.keySet());
        keySet.addAll(hostBlackList.keySet());
        return new ArrayList<>(keySet);
    }

    protected boolean startFixedScheduler() {
        return true;
    }

    @Override
    public boolean available(HostSpec host) {
        return (!hostBlackList.containsKey(host) || hostBlackList.get(host) <= System.currentTimeMillis() - blackListTimeOut) &&
                (!preBlackList.containsKey(host) || preBlackList.get(host) >= System.currentTimeMillis() - failOverTime);
    }

    @Override
    public void triggerValidate() {
        asyncValidate(orderHosts);
    }

    @Override
    public void destroy() {
        status = RouteStrategyStatus.destroy;
        try {
            if (fixedPeriodValidateService != null) {
                fixedPeriodValidateService.shutdown();
            }
        } catch (Throwable e) {
            // nothing to be done
        }

        try {
            fixed1sValidateService.shutdown();
        } catch (Throwable e) {
            // nothing to be done
        }

        try {
            preBlackList.clear();
        } catch (Throwable t) {
            preBlackList = new ConcurrentHashMap<>();
        }

        try {
            hostBlackList.clear();
        } catch (Throwable e) {
            hostBlackList = new ConcurrentHashMap<>();
        }

    }

    protected void asyncValidate(List<HostSpec> hostSpecs) {
        for (HostSpec host : hostSpecs) {
            if (configuredHosts.contains(host) && shouldValidate(host)) {
                asyncService.submit(() -> doAsyncValidate(host));
            }
        }
    }

    @Override
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    // return true : it's more than 1s from the last validate
    private boolean shouldValidate(HostSpec hostSpec) {
        synchronized (lastValidateMap) {
            Long timeNow = System.currentTimeMillis();
            if (timeNow - lastValidateMap.get(hostSpec) > ONE_SECOND) {
                lastValidateMap.put(hostSpec, timeNow);
                return true;
            }
            return false;
        }
    }

    protected Connection getConnection(HostSpec host) throws SQLException {
        try {
            return connectionFactory.getPooledConnectionForHost(host);
        } catch (SQLException e) {
            addToPreAbsentAndBlackPresent(host);
            throw e;
        }
    }

    private boolean isDestroy() {
        return RouteStrategyStatus.destroy.equals(status);
    }

    private void addToPreAbsent(HostSpec hostSpec) {
        if (hostSpec == null || isDestroy()) {
            return;
        }

        Long currentTime = System.currentTimeMillis();
        preBlackList.putIfAbsent(hostSpec, currentTime);
        LOGGER.warn(ADD_PRE_BLACK_LIST + ":" + hostSpec.toString());
        LOGGER.logEvent(CAT_LOG_TYPE, ADD_PRE_BLACK_LIST + ":" + hostSpec.toString(), preBlackList.toString());
    }

    private void addToBlackList(HostSpec hostSpec) {
        if (hostSpec == null || isDestroy()) {
            return;
        }

        Long currentTime = System.currentTimeMillis();
        hostBlackList.put(hostSpec, currentTime);
        LOGGER.warn(ADD_BLACK_LIST + ":" + hostSpec.toString());
        LOGGER.logEvent(CAT_LOG_TYPE, ADD_BLACK_LIST + ":" + hostSpec.toString(), hostBlackList.toString());
    }

    private void addToBlackListPresent(HostSpec hostSpec) {
        if (hostSpec == null || isDestroy()) {
            return;
        }

        Long currentTime = System.currentTimeMillis();
        if (hostBlackList.containsKey(hostSpec)) {
            hostBlackList.put(hostSpec, currentTime);
            LOGGER.warn(ADD_BLACK_LIST + ":" + hostSpec.toString());
            LOGGER.logEvent(CAT_LOG_TYPE, ADD_BLACK_LIST + ":" + hostSpec, hostBlackList.toString());
        }
    }

    private void removeFromPreBlackList(HostSpec hostSpec) {
        if (hostSpec == null || isDestroy()) {
            return;
        }

        Long last = preBlackList.remove(hostSpec);
        if (last != null) {
            LOGGER.info(REMOVE_PRE_BLACK_LIST + ":" + hostSpec.toString());
            LOGGER.logEvent(CAT_LOG_TYPE, REMOVE_PRE_BLACK_LIST + ":" + hostSpec.toString(), preBlackList.toString());
        }
    }

    private void removeFromBlackList(HostSpec hostSpec) {
        if (hostSpec == null || isDestroy()) {
            return;
        }

        Long last = hostBlackList.remove(hostSpec);
        if (last != null) {
            LOGGER.info(REMOVE_BLACK_LIST + ":" + hostSpec.toString());
            LOGGER.logEvent(CAT_LOG_TYPE, REMOVE_BLACK_LIST + ":" + hostSpec.toString(), hostBlackList.toString());
        }
    }

    protected void addToBlackAndRemoveFromPre(HostSpec hostSpec) {
        addToBlackList(hostSpec);
        removeFromPreBlackList(hostSpec);
    }

    private void addToPreAndRemoveFromBlack(HostSpec hostSpec) {
        addToPreAbsent(hostSpec);
        removeFromBlackList(hostSpec);
    }

    protected void removeFromAllBlackList(HostSpec hostSpec) {
        removeFromBlackList(hostSpec);
        removeFromPreBlackList(hostSpec);
    }

    protected void addToPreAbsentAndBlackPresent(HostSpec hostSpec) {
        addToPreAbsent(hostSpec);
        addToBlackListPresent(hostSpec);
    }

    abstract protected String getCatLogType();

    abstract protected void doAsyncValidate(HostSpec host);

}
