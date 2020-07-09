package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.ShardingCategory;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.helper.DefaultTableParser;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.helper.TableParser;

import java.util.*;

public class LogEntry implements ILogEntry{
    private static volatile ThreadLocal<String> currentCaller;

	private static String execludedPackageSpace = "com.ctrip.platform.dal.dao.";
	
	private boolean sensitive;
	private String[] sqls;
	private String[] pramemters;
	private String callString;
	private DalEventEnum event;
	private String errorMsg;
	private boolean success;
	private boolean transactional;
	private long duration;
	private String logicDbName;
	private String clusterName;
	private DatabaseCategory dbCategory;
	private String databaseName;
	private String dataBaseKeyName;
	private boolean isMaster;
	private ShardingCategory shardingCategory;
	private String shardId;
	private String serverAddress;
	private String dbUrl;
	private String commandType;
	private String userName;
	private int resultCount;
	private Integer affectedRows;
	private int[] affectedRowsArray;
	private long connectionCost;
	private String dao;
	private String method;
	private String source;
	private String clientVersion;
	private Set<String> tables=new HashSet<>();
	private static final String NOT_FOUND="NotFound";
	private Throwable exception;
	private TableParser tableParser = new DefaultTableParser();
	private static final String JSON_PATTERN = "{'Decode':'%s','Connect':'%s','Prepare':'%s','Excute':'%s','ClearUp':'%s'}";

	private String clientZone;
	private String dbZone;
	private String ucsValidation;
	private String dalValidation;

    /**
     * Internal performance recorder for performance cost in each stage.
     * As each low level DB operation will be logged once at ConnectionAction level, this recorder will
     * be set into LogEntry which is created as soon as ConnectionAction is created.
     */
    private long begin;
    private long beginConnect;
    private long endConnect;
    private long beginExecute;
    private long endExecute;
    private long end;
	
	public LogEntry(){
	    begin();

		StackTraceElement[] callers = Thread.currentThread().getStackTrace();
		for (int i = 4; i < callers.length; i++) {
			StackTraceElement caller = callers[i];
			if (caller.getClassName().startsWith(execludedPackageSpace))
				continue;
			
			dao = caller.getClassName();
			method = caller.getMethodName();
			source = caller.toString();
			break;
		}
	}
	
	public void setEvent(DalEventEnum event) {
		this.event = event;
		
		String commandType;
		
		if(this.event == DalEventEnum.CALL || 
				this.event == DalEventEnum.BATCH_CALL) {
			commandType = "SP";
		} else if(this.event == DalEventEnum.QUERY) {
			commandType = "Query";
		} else {
			commandType = "Execute";
		}
		
		setCommandType(commandType);
	}

	public boolean isSensitive() {
		return sensitive;
	}

	public void setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
	}
	
	public void setCallString(String callString) {
		this.callString = callString;
	}
	
	public String[] getSqls() {
		return sqls;
	}

	public void setSqls(String... sqls) {
		this.sqls = sqls;
	}

	public String[] getPramemters() {
		return pramemters;
	}

	public void setPramemters(String... pramemters) {
		this.pramemters = pramemters;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isTransactional() {
		return transactional;
	}

	public void setTransactional(boolean transactional) {
		this.transactional = transactional;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getLogicDbName() {
        return logicDbName;
    }

    public void setLogicDbName(String logicDbName) {
        this.logicDbName = logicDbName;
    }

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public DatabaseCategory getDbCategory() {
        return dbCategory;
    }

    public void setDbCategory(DatabaseCategory dbCategory) {
        this.dbCategory = dbCategory;
    }

    public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public Integer getAffectedRows() {
        return affectedRows;
    }

    public Integer setAffectedRows(Integer affectedRows) {
        this.affectedRows = affectedRows;
        return affectedRows;
    }

    public int[] getAffectedRowsArray() {
        return affectedRowsArray;
    }

    public int[] setAffectedRowsArray(int[] affectedRowsArray) {
        this.affectedRowsArray = affectedRowsArray;
        return affectedRowsArray;
    }

    public long getConnectionCost() {
        return connectionCost;
    }

    public void setConnectionCost(long connectionCost) {
        this.connectionCost = connectionCost;
    }

    public String getCallString() {
		return callString;
	}

	public DalEventEnum getEvent() {
		return event;
	}
	
	public String getDao() {
		return dao;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getSource() {
		return source;
	}

	public String getDataBaseKeyName() {
		return dataBaseKeyName;
	}

	public void setDataBaseKeyName(String dataBaseKeyName) {
		this.dataBaseKeyName = dataBaseKeyName;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}
	
	public void setShardId(String shardId) {
		this.shardId = shardId;
	}
	
	public String getShardId() {
		return shardId;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

    public int getSqlSize() {
		int size = 0;
		if (this.event == DalEventEnum.QUERY
				|| this.event == DalEventEnum.UPDATE_SIMPLE
				|| this.event == DalEventEnum.UPDATE_KH
				|| this.event == DalEventEnum.BATCH_UPDATE_PARAM) {
			size = null != this.sqls && this.sqls.length > 0 ? this.sqls[0].length() : 0;
		}
		if (this.event == DalEventEnum.BATCH_UPDATE) {
			for (String sqll : this.sqls) {
				size += sqll.length();
			}
		}
		if (this.event == DalEventEnum.CALL
				|| this.event == DalEventEnum.BATCH_CALL) {
			size = this.callString.length();
		}

		return size;
	}

    public void begin(){
        begin = System.currentTimeMillis();
    }

    public void beginConnect(){
        beginConnect = System.currentTimeMillis();
    }

    public void endConnect(){
        endConnect = System.currentTimeMillis();
    }

    public void beginExecute(){
        beginExecute = System.currentTimeMillis();
    }

    public void endExectue(){
        endExecute = System.currentTimeMillis();
    }

	public String getCostDetail() {
		// Final end
		end = System.currentTimeMillis();
		Map<String, String> costDetailMap = new LinkedHashMap<>();
		costDetailMap.put("'Decode'", begin == 0 ? "'0'" : "'" + Long.toString(beginConnect - begin) + "'");
		costDetailMap.put("'Connect'", "'" + Long.toString(endConnect - beginConnect) + "'");
		costDetailMap.put("'Prepare'", "'" + Long.toString(beginExecute - endConnect) + "'");
		costDetailMap.put("'Execute'", "'" + Long.toString(endExecute - beginExecute) + "'");
		costDetailMap.put("'ClearUp'", "'" + Long.toString(end - endExecute) + "'");
		return costDetailMap.toString();
	}

    /**
     * @return Current caller
     */
	public String getCaller() {
        String sqlType = getDao() + "." + getMethod();
        
        // If comes from internal executor
        if(sqlType.startsWith("java.util.concurrent.FutureTask"))
            sqlType = currentCaller.get();
        
        return sqlType;
    }
	
	public String getCallerInShort() {
	    try {
            String caller = getCaller();
            int lastIndex = caller.lastIndexOf('.');
            
            lastIndex = caller.lastIndexOf('.', lastIndex - 1);
            return caller.substring(lastIndex + 1);
        } catch (Throwable e) {
            return "Error!! Can Not Locate Calller";
        }
	}
	
    /**
     * Put curent caller into threadlocal to allow ConnectionAction get caller in later stage
     */
    public static void populateCurrentCaller(String caller) {
        currentCaller.set(caller);
    }
    
    /**
     * Clear curent caller of threadlocal
     */
    public static void clearCurrentCaller() {
        currentCaller.remove();
    }

    public synchronized static void init(){
        if(currentCaller != null)
            return;
        
        currentCaller = new ThreadLocal<>();
    }

    public synchronized static void shutdown() {
        if(currentCaller == null)
            return;
        
        currentCaller.remove();
        currentCaller = null;
    }

	public Set<String> extractTablesFromSqls() {
		Set<String> tables = new HashSet<>();
		if (sqls != null)
			tables = tableParser.getTablesFromSqls(sqls);
		if (callString != null)
			tables = tableParser.getTablesFromSqls(callString);
		if (tables.size() == 0)
			tables.add(NOT_FOUND);
		return tables;
	}

	public void setTables(Set<String> tables) {
		if (tables == null || tables.size() == 0) {
			this.tables = extractTablesFromSqls();
			return;
		}
		this.tables = tables;
	}

	public Set<String> getTables(){
		return tables;
	}

	public ShardingCategory getShardingCategory() {
		return shardingCategory;
	}

	public void setShardingCategory(ShardingCategory shardingCategory) {
		this.shardingCategory = shardingCategory;
	}

	public String getClientZone() {
		return clientZone;
	}

	public void setClientZone(String clientZone) {
		this.clientZone = clientZone;
	}

	public String getDbZone() {
		return dbZone;
	}

	public void setDbZone(String dbZone) {
		this.dbZone = dbZone;
	}

	public String getUcsValidation() {
		return ucsValidation;
	}

	public void setUcsValidation(String ucsValidation) {
		this.ucsValidation = ucsValidation;
	}

	public String getDalValidation() {
		return dalValidation;
	}

	public void setDalValidation(String dalValidation) {
		this.dalValidation = dalValidation;
	}

}
