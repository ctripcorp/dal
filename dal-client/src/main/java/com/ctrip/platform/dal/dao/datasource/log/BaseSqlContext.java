package com.ctrip.platform.dal.dao.datasource.log;

import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.ValidationResult;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ctrip.platform.dal.dao.log.LogUtils.getLogContext;

/**
 * @author c7ch23en
 */
public abstract class BaseSqlContext implements SqlContext {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final EnvUtils ENV_UTILS = DalElementFactory.DEFAULT.getEnvUtils();
    private static final DalPropertiesLocator dalPropertiesLocator = DalPropertiesManager.getInstance().getDalPropertiesLocator();
    private static final Set<String> daoPackages = dalPropertiesLocator.getDaoPackagesPath();

    private static final String METRIC_NAME = "arch.dal.sql.cost";
    private static final long TICKS_PER_MILLISECOND = 10000;
    private static final String[] CALLER_EXCLUDED_PACKAGES = new String[] {
            "com.ctrip.platform.dal.dao",
            "com.ctrip.datasource"
    };

    protected static final String CHANNEL = "Channel";
    protected static final String CLIENT = "Client";
    protected static final String CLIENT_ZONE = "ClientZone";
    protected static final String DB_NAME = "DBName";
    protected static final String DB_ZONE = "DBZone";
    protected static final String TABLES = "Tables";
    protected static final String DAO = "DAO";
    protected static final String METHOD = "Method";
    protected static final String OP_TYPE = "OperationType";
    protected static final String UCS_VALIDATION = "UcsValidation";
    protected static final String DAL_VALIDATION = "DalValidation";
    protected static final String STATUS = "Status";

    protected static final String CHANNEL_DATASOURCE = "DAL.DataSource";
    protected static final String STATUS_FAIL = "fail";
    protected static final String STATUS_SUCCESS = "success";
    protected static final String UNDEFINED = "undefined";

    private final String clientVersion;
    private final String clientZone;
    private String dbName;
    private String dbZone;
    private Set<String> tables;
    private String callerClass;
    private String callerMethod;
    private OperationType operation;

    private long executionStartTime;
    private String ucsValidation;
    private String dalValidation;
    private Throwable errorIfAny;
    private long executionEndTime;
    private long sqlTransactionStartTime;
    private long connectionObtainedTime;
    private long recordRows;
    private String sql;
    private String database;
    private String params;
    private boolean encryptParams = true;

    public BaseSqlContext() {
        this(null);
    }

    public BaseSqlContext(String dbName) {
        this(Version.getVersion(), ENV_UTILS.getZone(), dbName);
    }

    public BaseSqlContext(String clientVersion, String clientZone) {
        this(clientVersion, clientZone, null);
    }

    public BaseSqlContext(String clientVersion, String clientZone, String dbName) {
        this.clientVersion = clientVersion;
        this.clientZone = clientZone;
        this.dbName = dbName;
    }

    @Override
    public void populateDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public void populateDbZone(String dbZone) {
        this.dbZone = dbZone;
    }

    @Override
    public void populateCaller() {
        try {
            StackTraceElement[] callers = Thread.currentThread().getStackTrace();
            for (int i = 2; i < callers.length; i++) {
                StackTraceElement caller = callers[i];
                boolean excluded = false;
                for (String exPkg : CALLER_EXCLUDED_PACKAGES)
                    if (caller.getClassName().startsWith(exPkg)) {
                        excluded = true;
                        break;
                    }
                if (!excluded && includeAssignedPackages(caller)) {
                    populateCaller(caller.getClassName(), caller.getMethodName());
                    break;
                }
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    protected boolean includeAssignedPackages(StackTraceElement caller) {
        for (String pack : daoPackages) {
            if (caller.getClassName().startsWith(pack))
                return true;
        }
        return false;
    }

    @Override
    public void populateCaller(String callerClass, String callerMethod) {

        this.callerClass = callerClass;
        this.callerMethod = callerMethod;
    }

    @Override
    public void populateOperationType(OperationType operation) {
        this.operation = operation;
    }

    @Override
    public void populateTables(Set<String> tables) {
        this.tables = tables;
    }

    @Override
    public void startExecution() {
        executionStartTime = System.currentTimeMillis();
    }

    @Override
    public void populateValidationResult(ValidationResult result) {
        if (result != null) {
            ucsValidation = result.getUcsValidationMessage();
            dalValidation = result.getDalValidationMessage();
        }
    }

    @Override
    public void endExecution() {
        executionEndTime = System.currentTimeMillis();
    }

    @Override
    public void endExecution(Throwable errorIfAny) {
        try {
            this.errorIfAny = errorIfAny;
            endExecution();
            logMetric();
        } finally {
            if (!getLogContext().isHasLogged())
                logSqlTransaction();
        }
    }

    @Override
    public void populateQueryRows(int rows) {
        this.recordRows = rows;
    }

    @Override
    public void populateDatabase(String database) {
        this.database = database;
    }

    @Override
    public void populateSql(String sql) {
        this.sql = sql;
    }

    protected void logSqlTransaction() {
        LOGGER.logSqlTransaction(this);
    }

    @Override
    public void populateSqlTransaction(long millionSeconds) {
        this.sqlTransactionStartTime = millionSeconds;
    }

    @Override
    public void populateParameters(List<StatementParameters> parameters) {
        StringBuilder sb = new StringBuilder();

        for (StatementParameters param : parameters) {
            sb.append(param.toLogString());
        }
        params = sb.toString();
    }

    @Override
    public void populateConnectionObtained(long millionSeconds) {
        this.connectionObtainedTime = millionSeconds;
    }

    @Override
    public void populateEncryptParams(boolean isEncrypt) {
        this.encryptParams = isEncrypt;
    }

    public boolean isEncryptParams() {
        return encryptParams;
    }

    protected void logMetric() {
        LOGGER.logMetric(METRIC_NAME, getExecutionTime() * TICKS_PER_MILLISECOND, toMetricTags());
    }

    protected long getExecutionTime() {
        return executionEndTime > executionStartTime ? executionEndTime - executionStartTime : 0;
    }

    protected Map<String, String> toMetricTags() {
        Map<String, String> tags = new HashMap<>();
        addTag(tags, CHANNEL, CHANNEL_DATASOURCE);
        addTag(tags, CLIENT, clientVersion);
        addTag(tags, CLIENT_ZONE, clientZone);
        addTag(tags, DB_NAME, dbName);
        addTag(tags, DB_ZONE, dbZone);
        addTag(tags, TABLES, tables);
        addTag(tags, DAO, callerClass);
        addTag(tags, METHOD, callerMethod);
        addTag(tags, OP_TYPE, operation);
        addTag(tags, UCS_VALIDATION, ucsValidation);
        addTag(tags, DAL_VALIDATION, dalValidation);
        addTag(tags, STATUS, errorIfAny != null ? STATUS_FAIL : STATUS_SUCCESS);
        return tags;
    }

    protected void addTag(Map<String, String> tags, String tagName, Object tagValue) {
        if (tagValue == null) {
            tags.put(tagName, UNDEFINED);
            return;
        }
        if (tagValue instanceof String)
            tags.put(tagName, (String) tagValue);
        else if (tagValue instanceof OperationType)
            tags.put(tagName, ((OperationType) tagValue).name());
    }

    protected String getClientVersion() {
        return clientVersion;
    }

    protected String getClientZone() {
        return clientZone;
    }

    protected String getDbName() {
        return dbName;
    }

    protected String getDbZone() {
        return dbZone;
    }

    public long getSqlTransactionStartTime() {
        return sqlTransactionStartTime;
    }

    public void setSqlTransactionStartTime(long sqlTransactionStartTime) {
        this.sqlTransactionStartTime = sqlTransactionStartTime;
    }

    public Set<String> getTables() {
        return tables;
    }

    public String getCallerClass() {
        return callerClass;
    }

    public String getCallerMethod() {
        return callerMethod;
    }

    public OperationType getOperation() {
        return operation;
    }

    public long getExecutionStartTime() {
        return executionStartTime;
    }

    public String getUcsValidation() {
        return ucsValidation;
    }

    public String getDalValidation() {
        return dalValidation;
    }

    public Throwable getErrorIfAny() {
        return errorIfAny;
    }

    public long getExecutionEndTime() {
        return executionEndTime;
    }

    public String getSql() {
        return sql;
    }

    public long getRecordRows() {
        return recordRows;
    }

    public String getDatabase() {
        return database;
    }

    public String getParams() {
        return params;
    }

    public String getCaller() {
        return callerClass + callerMethod;
    }

    public long getConnectionObtainedTime() {
        return connectionObtainedTime;
    }

    public void setConnectionObtainedTime(long connectionObtainedTime) {
        this.connectionObtainedTime = connectionObtainedTime;
    }
}
