package com.ctrip.platform.dal.dao.datasource.log;

import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.datasource.ValidationResult;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public abstract class BaseSqlContext implements SqlContext {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final EnvUtils ENV_UTILS = DalElementFactory.DEFAULT.getEnvUtils();

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
    protected static final String READ_STRATEGY = "ReadStrategy";

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
    private String readStrategy;

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
                if (!excluded) {
                    populateCaller(caller.getClassName(), caller.getMethodName());
                    break;
                }
            }
        } catch (Throwable t) {
            // ignore
        }
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
        this.errorIfAny = errorIfAny;
        endExecution();
        logMetric();
    }

    @Override
    public void populateReadStrategy(String readStrategy) {
        this.readStrategy = readStrategy;
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
        addTag(tags, READ_STRATEGY, readStrategy);
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

    public String getReadStrategy() {
        return readStrategy;
    }

    public void setReadStrategy(String readStrategy) {
        this.readStrategy = readStrategy;
    }
}
