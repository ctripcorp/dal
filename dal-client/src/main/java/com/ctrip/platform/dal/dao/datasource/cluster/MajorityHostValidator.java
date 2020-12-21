package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.HostAndPort;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MajorityHostValidator implements ConnectionValidator, HostValidator {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CAT_LOG_TYPE = "DAL.pickConnection";
    private static final String FIND_WRONG_HOST_SPEC = "Validator::findWrongHostSpec";
    private static final String CONNECTION_URL = "Validator::getConnectionUrl";
    private static final String DEFAULT = "default";
    private static final String ADD_BLACK_LIST = "Validator::addToBlackList";
    private static final String ADD_PRE_BLACK_LIST = "Validator::addToPreBlackList";
    private static final String REMOVE_BLACK_LIST = "Validator::removeFromBlackList";
    private static final String REMOVE_PRE_BLACK_LIST = "Validator::removeFromPreBlackList";
    private static final String VALIDATE_COMMAND_DENIED = "Validator::validateCommandDenied";
    private static final String VALIDATE_ERROR = "Validator::validateError";
    private static final String VALIDATE_RESULT = "Validator::validateResult:";
    private static final String VALIDATE_RESULT_DETAIL ="Validator::validateResultDetail:MEMBER_ID=%s MEMBER_STATE=%s CURRENT_MEMBER_ID=%s";

    private volatile Set<HostSpec> configuredHosts;
    private volatile List<HostSpec> orderHosts;
    private volatile long failOverTime;
    private volatile long blackListTimeOut;
    private volatile long fixedValidatePeriod = 30000;
    private volatile ConnectionFactory factory;
    private volatile HashMap<HostSpec, Long> lastValidateMap = new HashMap<>();
    private volatile ScheduledExecutorService fixedPeriodValidateService = Executors.newSingleThreadScheduledExecutor();
    private volatile ScheduledExecutorService fixed1sValidateService = Executors.newSingleThreadScheduledExecutor();
    private static ValidateResult defaultValidateResult = new ValidateResult();
    private static volatile ExecutorService asyncService = Executors.newFixedThreadPool(4);
    private static volatile ExecutorService doubleCheckService = Executors.newFixedThreadPool(2);
    private static volatile ConcurrentHashMap<HostSpec, Long> hostBlackList = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<HostSpec, Long> preBlackList = new ConcurrentHashMap<>();
    private final Long ONE_SECOND = 900L; // 100ms threshold to tolerant schedule time fault
    private static final String validateSQL1 = "select members.MEMBER_STATE MEMBER_STATE, " +
            "members.MEMBER_ID MEMBER_ID, " +
            "member_stats.MEMBER_ID CURRENT_MEMBER_ID " +
            "from performance_schema.replication_group_members members left join performance_schema.replication_group_member_stats member_stats on member_stats.MEMBER_ID=members.MEMBER_ID;";

    private enum MemberState{
        Online, Error, Offline, Recovering
    }

    private enum Columns {
        MEMBER_STATE, MEMBER_ID, CURRENT_MEMBER_ID
    }

    protected static class ValidateResult{
        public boolean validateResult = true;
        public String currentMemberId = "";

        public ValidateResult(boolean validateResult, String currentMemberId) {
            this.validateResult = validateResult;
            this.currentMemberId = currentMemberId;
        }

        public ValidateResult() {
        }

        @Override
        public String toString() {
            return validateResult + "";
        }
    }

    public MajorityHostValidator() {
        fixedScheduleStart();
    }

    public MajorityHostValidator(ConnectionFactory factory, Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        this();
        this.factory = factory;
        this.configuredHosts = configuredHosts;
        this.failOverTime = failOverTime;
        this.blackListTimeOut = blackListTimeOut;
        this.orderHosts = orderHosts;
        this.fixedValidatePeriod = fixedValidatePeriod;
        init();
    }

    private void init() {
        initLastValidateMap();
    }

    private void initLastValidateMap() {
        for (HostSpec host : orderHosts) {
            lastValidateMap.put(host, System.currentTimeMillis());
        }
    }

    private void fixedScheduleStart() {
        try {
            fixedPeriodValidateService.scheduleAtFixedRate(() -> asyncValidate(orderHosts), 1000, fixedValidatePeriod, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.error("start schedule1 error", e);
        }

        try {
            fixed1sValidateService.scheduleAtFixedRate(() -> {
                Set<HostSpec> keySet = new HashSet<>(preBlackList.keySet());
                keySet.addAll(hostBlackList.keySet());
                asyncValidate(new ArrayList<>(keySet));
            }, 1000, 1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.error("start schedule2 error", e);
        }
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
        if (!fixedPeriodValidateService.isShutdown()) {
            fixedPeriodValidateService.shutdown();
        }

        if (!fixed1sValidateService.isShutdown()) {
            fixed1sValidateService.shutdown();
        }
    }

    @Override
    public boolean validate(HostConnection connection) throws SQLException {
        try {
            HostSpec currentHost = connection.getHost();
            boolean validateResult = validateAndUpdate(connection, currentHost, configuredHosts.size());
            if (!validateResult) {
                asyncValidate(orderHosts);
            }
            return validateResult;
        } catch (SQLException e) {
            asyncValidate(orderHosts);
            throw e;
        }
    }

    protected HostSpec getHostSpecFromConnection(Connection connection) {
        String urlForLog;
        try {
            urlForLog = connection.getMetaData().getURL();
        } catch (SQLException e) {
            LOGGER.error(CONNECTION_URL, e);
            return null;
        }

        HostAndPort hostAndPort = ConnectionStringParser.parseHostPortFromURL(urlForLog);
        if (StringUtils.isEmpty(hostAndPort.getHost()) || hostAndPort.getPort() == null) {
            LOGGER.warn(FIND_WRONG_HOST_SPEC + ":" + urlForLog);
            LOGGER.logEvent(CAT_LOG_TYPE, FIND_WRONG_HOST_SPEC, urlForLog);
            return null;
        }

        return new HostSpec(hostAndPort.getHost(), hostAndPort.getPort(), DEFAULT);
    }

    protected boolean validateAndUpdate(Connection connection, HostSpec currentHost, int clusterHostCount) throws SQLException {
        try {
            ValidateResult validateResult = validate(connection, clusterHostCount);
            LOGGER.info(VALIDATE_RESULT +  currentHost.toString() + ":" + validateResult);
            LOGGER.logEvent(CAT_LOG_TYPE, currentHost.toString() + ":" + validateResult, null);
            if (validateResult.validateResult) {
                // memberId is not empty and this host is in list and another host think it is online
                if (!StringUtils.isEmpty(validateResult.currentMemberId) &&
                        (preBlackList.containsKey(currentHost) || hostBlackList.containsKey(currentHost)) &&
                        doubleCheckOnlineStatus(validateResult.currentMemberId, currentHost))
                    removeFromAllBlackList(currentHost);
                return true;
            } else {
                addToBlackAndRemoveFromPre(currentHost);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.warn(VALIDATE_ERROR, e);
            LOGGER.logEvent(CAT_LOG_TYPE, currentHost + ":error", e.getMessage());
            addToPreAbsentAndBlackPresent(currentHost);
            throw e;
        }
    }

    protected boolean doubleCheckOnlineStatus(String currentMemberId, HostSpec currentHostSpec) {
        CountDownLatch latch = new CountDownLatch(1);
        List<FutureTask<Boolean>> futureTaskList = new ArrayList<>();
        AtomicInteger onlineCount = new AtomicInteger(1);
        AtomicInteger finishedCount = new AtomicInteger(0);
        for (HostSpec hostSpec : configuredHosts) {
            if (!currentHostSpec.equals(hostSpec)) {
                FutureTask futureTask = new FutureTask(() -> {
                    try (Connection connection = getConnection(hostSpec)){
                        boolean result = doubleCheckValidate(connection, currentMemberId);
                        if (result)
                            onlineCount.incrementAndGet();
                    }catch (Exception e) {
                        LOGGER.error("doubleCheckOnlineStatus", e);
                    } finally {
                        finishedCount.incrementAndGet();
                        // online count is more than half or failed count is more than half
                        if (onlineCount.get() * 2 > configuredHosts.size() || (finishedCount.get() - onlineCount.get()) * 2 > configuredHosts.size())
                            latch.countDown();
                    }
                    return true;
                });
                futureTask.run();
                futureTaskList.add(futureTask);
                doubleCheckService.submit(futureTask);
            }
        }

        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (Exception e) {

        }

        for (FutureTask futureTask : futureTaskList) {
            futureTask.cancel(true);
        }

        return onlineCount.get() * 2 > configuredHosts.size();
    }

    protected void asyncValidate(List<HostSpec> hostSpecs) {
        for (HostSpec host : hostSpecs) {
            if (configuredHosts.contains(host) && shouldValidate(host)) {
                asyncService.submit(() -> {
                    try (Connection connection = getConnection(host)){
                        validateAndUpdate(connection, host, configuredHosts.size());
                    }catch (Throwable e) {
                        LOGGER.error(CAT_LOG_TYPE, e);
                    }
                });
            }
        }
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
            return factory.createConnectionForHost(host);
        } catch (SQLException e) {
            addToPreAbsentAndBlackPresent(host);
            throw e;
        }
    }

    protected ValidateResult validate(Connection connection, int clusterHostCount) throws SQLException {
        boolean currentHostState = false;
        String currentMemberId = "";
        int onlineCount = 0;

        try(Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(1);
            try(ResultSet resultSet = statement.executeQuery(validateSQL1)) {
                while (resultSet.next()) {
                    String memberId = resultSet.getString(Columns.MEMBER_ID.name());
                    currentMemberId = resultSet.getString(Columns.CURRENT_MEMBER_ID.name());
                    String memberState = resultSet.getString(Columns.MEMBER_STATE.name());
                    LOGGER.info(String.format(VALIDATE_RESULT_DETAIL, memberId, memberState, currentMemberId));
                    if (memberId.equals(currentMemberId)) {
                        currentHostState = MemberState.Online.name().equalsIgnoreCase(memberState);
                    }
                    if (MemberState.Online.name().equalsIgnoreCase(memberState)) {
                        onlineCount++;
                    }
                }
            } catch (MySQLSyntaxErrorException e) {
                LOGGER.warn(VALIDATE_COMMAND_DENIED, e);
                LOGGER.logEvent(CAT_LOG_TYPE, VALIDATE_COMMAND_DENIED, e.getMessage());
                return defaultValidateResult;
            }
        }

        return currentHostState && 2 * onlineCount > clusterHostCount ? new ValidateResult(true, currentMemberId) : new ValidateResult(false, currentMemberId);
    }

    protected boolean doubleCheckValidate(Connection connection, String validateMemberId) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(1);
            try(ResultSet resultSet = statement.executeQuery(validateSQL1)) {
                while (resultSet.next()) {
                    String memberId = resultSet.getString(Columns.MEMBER_ID.name());
                    String memberState = resultSet.getString(Columns.MEMBER_STATE.name());
                    String currentMemberId = resultSet.getString(Columns.CURRENT_MEMBER_ID.name());
                    LOGGER.info(String.format(VALIDATE_RESULT_DETAIL, memberId, memberState, currentMemberId));
                    if (validateMemberId.equalsIgnoreCase(memberId)) {
                        return  MemberState.Online.name().equalsIgnoreCase(memberState);
                    }
                }
            } catch (MySQLSyntaxErrorException e) {
                LOGGER.warn(VALIDATE_COMMAND_DENIED, e);
                LOGGER.logEvent(CAT_LOG_TYPE, VALIDATE_COMMAND_DENIED, e.getMessage());
                return false;
            }
        }

        return false;
    }

    private void addToPreAbsent(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        LOGGER.warn(ADD_PRE_BLACK_LIST + ":" + hostSpec.toString());
        LOGGER.logEvent(CAT_LOG_TYPE, ADD_PRE_BLACK_LIST, hostSpec.toString());
        Long currentTime = System.currentTimeMillis();
        preBlackList.putIfAbsent(hostSpec, currentTime);
    }

    private void addToBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        LOGGER.warn(ADD_BLACK_LIST + ":" + hostSpec.toString());
        LOGGER.logEvent(CAT_LOG_TYPE, ADD_BLACK_LIST, hostSpec.toString());
        Long currentTime = System.currentTimeMillis();
        hostBlackList.put(hostSpec, currentTime);
    }

    private void addToBlackListPresent(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        Long currentTime = System.currentTimeMillis();
        if (hostBlackList.containsKey(hostSpec)) {
            LOGGER.warn(ADD_BLACK_LIST + ":" + hostSpec.toString());
            LOGGER.logEvent(CAT_LOG_TYPE, ADD_BLACK_LIST, hostSpec.toString());
            hostBlackList.put(hostSpec, currentTime);
        }
    }

    private void removeFromPreBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        Long last = preBlackList.remove(hostSpec);
        if (last != null) {
            LOGGER.info(REMOVE_PRE_BLACK_LIST + ":" + hostSpec.toString());
            LOGGER.logEvent(CAT_LOG_TYPE, REMOVE_PRE_BLACK_LIST, hostSpec.toString());
        }
    }

    private void removeFromBlackList(HostSpec hostSpec) {
        if (hostSpec == null) {
            return;
        }

        Long last = hostBlackList.remove(hostSpec);
        if (last != null) {
            LOGGER.info(REMOVE_BLACK_LIST + ":" + hostSpec.toString());
            LOGGER.logEvent(CAT_LOG_TYPE, REMOVE_BLACK_LIST, hostSpec.toString());
        }
    }

    private void addToBlackAndRemoveFromPre(HostSpec hostSpec) {
        addToBlackList(hostSpec);
        removeFromPreBlackList(hostSpec);
    }

    private void addToPreAndRemoveFromBlack(HostSpec hostSpec) {
        addToPreAbsent(hostSpec);
        removeFromBlackList(hostSpec);
    }

    private void removeFromAllBlackList(HostSpec hostSpec) {
        removeFromBlackList(hostSpec);
        removeFromPreBlackList(hostSpec);
    }

    private void addToPreAbsentAndBlackPresent(HostSpec hostSpec) {
        addToPreAbsent(hostSpec);
        addToBlackListPresent(hostSpec);
    }

}
