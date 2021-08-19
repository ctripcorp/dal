package com.ctrip.platform.dal.dao.datasource.cluster.validator;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MajorityHostValidator extends AbstractHostValidator implements HostValidator {

    private static final String CAT_LOG_TYPE = "DAL.mgr";
    private static final String VALIDATE_COMMAND_DENIED = "Validator::validateCommandDenied";
    private static final String VALIDATE_ERROR = "Validator::validateError";
    private static final String VALIDATE_RESULT = "Validator::validateResult:";
    private static final String ASYNC_VALIDATE_RESULT = "Validator::asyncValidateResult:";
    private static final String VALIDATE_RESULT_DETAIL ="Validator::validateResultDetail:MEMBER_ID=%s MEMBER_STATE=%s CURRENT_MEMBER_ID=%s";
    private static final String DOUBLE_CHECK_VALIDATE_RESULT_DETAIL ="Validator::doubleCheckValidateResultDetail:MEMBER_ID=%s MEMBER_STATE=%s CURRENT_MEMBER_ID=%s";

    private static ValidateResult defaultValidateResult = new ValidateResult();
    private static volatile ExecutorService doubleCheckService = Executors.newFixedThreadPool(2);
    private static volatile ConcurrentHashMap<HostSpec, Long> hostBlackList = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<HostSpec, Long> preBlackList = new ConcurrentHashMap<>();
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

    protected static class ValidateResult {
        public boolean validateResult = true;
        public String currentMemberId = "";
        public String message;

        public ValidateResult(boolean validateResult, String currentMemberId, String message) {
            this.validateResult = validateResult;
            this.currentMemberId = currentMemberId;
            this.message = message;
        }

        public ValidateResult(boolean validateResult, String currentMemberId) {
            this.validateResult = validateResult;
            this.currentMemberId = currentMemberId;
        }

        public ValidateResult() {
        }

        @Override
        public String toString() {
            return "ValidateResult{" +
                    "validateResult=" + validateResult +
                    ", currentMemberId='" + currentMemberId + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    public MajorityHostValidator(ConnectionFactory factory, Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        super(factory, configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    @Override
    public boolean validate(HostConnection connection) throws SQLException {
        try {
            HostSpec currentHost = connection.getHost();
            ValidateResult validateResult = validateAndUpdate(connection, currentHost, configuredHosts.size());
            LOGGER.info(VALIDATE_RESULT +  currentHost.toString() + ":" + validateResult.validateResult);
            LOGGER.logEvent(CAT_LOG_TYPE, currentHost.toString() + ":" + validateResult.validateResult, validateResult.toString());
            if (!validateResult.validateResult) {
                asyncValidate(orderHosts);
            }
            return validateResult.validateResult;
        } catch (SQLException e) {
            asyncValidate(orderHosts);
            throw e;
        }
    }

    protected ValidateResult validateAndUpdate(Connection connection, HostSpec currentHost, int clusterHostCount) throws SQLException {
        try {
            ValidateResult validateResult = validate(connection, clusterHostCount);
            if (validateResult.validateResult) {
                // memberId is not empty and this host is in list and another host think it is online
                if (!StringUtils.isEmpty(validateResult.currentMemberId) &&
                        (preBlackList.containsKey(currentHost) || hostBlackList.containsKey(currentHost)) &&
                        doubleCheckOnlineStatus(validateResult.currentMemberId, currentHost))
                    removeFromAllBlackList(currentHost);
            } else {
                addToBlackAndRemoveFromPre(currentHost);
            }
            return validateResult;
        } catch (SQLException e) {
            LOGGER.warn(VALIDATE_ERROR, e);
            LOGGER.logEvent(CAT_LOG_TYPE, currentHost + ":unknown", e.getMessage());
            addToPreAbsentAndBlackPresent(currentHost);
            throw e;
        }
    }

    protected boolean doubleCheckOnlineStatus(String currentMemberId, HostSpec currentHostSpec) {
        CountDownLatch latch = new CountDownLatch(1);
        List<Future> futures = new ArrayList<>();
        AtomicInteger onlineCount = new AtomicInteger(1);
        AtomicInteger finishedCount = new AtomicInteger(1);
        for (HostSpec hostSpec : configuredHosts) {
            if (!currentHostSpec.equals(hostSpec)) {
                Future future = doubleCheckService.submit(() -> {
                    try (Connection connection = getConnection(hostSpec)){
                        boolean result = doubleCheckValidate(connection, currentMemberId);
                        if (result)
                            onlineCount.incrementAndGet();
                    }catch (Exception e) {
                        LOGGER.warn("doubleCheckOnlineStatus", e);
                    } finally {
                        finishedCount.incrementAndGet();
                        // online count is more than half or failed count is more than half
                        if (onlineCount.get() * 2 > configuredHosts.size() || (finishedCount.get() - onlineCount.get()) * 2 > configuredHosts.size())
                            latch.countDown();
                    }
                });
                futures.add(future);
            }
        }

        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (Throwable e) {

        }

        for (Future future : futures) {
            try {
                future.cancel(true);
            } catch (Throwable t) {

            }
        }

        return onlineCount.get() * 2 > configuredHosts.size();
    }

    @Override
    protected void doAsyncValidate(HostSpec host) {
        try (Connection connection = getConnection(host)){
            ValidateResult validateResult = validateAndUpdate(connection, host, configuredHosts.size());
            LOGGER.info(ASYNC_VALIDATE_RESULT + validateResult);
        }catch (Throwable e) {
            LOGGER.warn(CAT_LOG_TYPE, e);
        }
    }

    protected ValidateResult validate(Connection connection, int clusterHostCount) throws SQLException {
        boolean currentHostState = false;
        String outputMemberId = "";
        int onlineCount = 0;
        StringBuilder message = new StringBuilder();

        try(Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(1);
            try(ResultSet resultSet = statement.executeQuery(validateSQL1)) {
                while (resultSet.next()) {
                    String memberId = resultSet.getString(Columns.MEMBER_ID.name());
                    String currentMemberId = resultSet.getString(Columns.CURRENT_MEMBER_ID.name());
                    String memberState = resultSet.getString(Columns.MEMBER_STATE.name());
                    message.append(String.format(VALIDATE_RESULT_DETAIL, memberId, memberState, currentMemberId));
                    if (memberId.equals(currentMemberId)) {
                        outputMemberId = currentMemberId;
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

        return currentHostState && 2 * onlineCount > clusterHostCount ? new ValidateResult(true, outputMemberId, message.toString()) : new ValidateResult(false, outputMemberId, message.toString());
    }

    protected boolean doubleCheckValidate(Connection connection, String validateMemberId) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(1);
            try(ResultSet resultSet = statement.executeQuery(validateSQL1)) {
                boolean flag = false;
                StringBuilder message = new StringBuilder("validateMemberId:" + validateMemberId);
                while (resultSet.next()) {
                    String memberId = resultSet.getString(Columns.MEMBER_ID.name());
                    String memberState = resultSet.getString(Columns.MEMBER_STATE.name());
                    String currentMemberId = resultSet.getString(Columns.CURRENT_MEMBER_ID.name());
                    message.append(String.format(DOUBLE_CHECK_VALIDATE_RESULT_DETAIL, memberId, memberState, currentMemberId));
                    if (validateMemberId.equalsIgnoreCase(memberId)) {
                        flag = MemberState.Online.name().equalsIgnoreCase(memberState);
                    }
                }
                LOGGER.info("doubleCheckValidate:" + message.toString());
                return flag;
            } catch (MySQLSyntaxErrorException e) {
                LOGGER.warn(VALIDATE_COMMAND_DENIED, e);
                LOGGER.logEvent(CAT_LOG_TYPE, VALIDATE_COMMAND_DENIED, e.getMessage());
                return false;
            }
        }catch (Exception e) {
            LOGGER.warn("error occured while doubleCheckValidate:", e);
        }

        return false;
    }
}
