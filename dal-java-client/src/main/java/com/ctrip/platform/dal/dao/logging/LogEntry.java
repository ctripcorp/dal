package com.ctrip.platform.dal.dao.logging;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalDirectClient;

public class LogEntry {
    private static String SQLHIDDENString = "*";
    //DateTime
    private Date timeStamp;
    private String machine;
    private String sql;
    private String sqlHash;
    private boolean sensitive;
    private String inputParamStr;
    private String outputParamStr = "";
    
    private String logicDbName;
    private String realDbName;
    private String dao;
    private String method;
    private String source;
    private int eventId;
    
    public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	private String message;
    private String level;
    private String title;
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private long duration; 
    private String userName;
    private String serverAddress;
    private String databaseName;
    private boolean transactional;
    private int resultCount;
    private String commandType;

    public LogEntry(String logName) 
    {
    	this.timeStamp = new Date();
    	this.machine = CommonUtil.MACHINE;
    }
    
	/**
     * 
     * @param sql
     * @param parameters
     * @param dbName logic db name and real db name.
     * @param eId
     * @param message
     */
    public LogEntry(String sql, StatementParameters parameters, String logicDbName, String realDbName, int eId, String message)
    {
    	this.sql = sql;
        timeStamp = new Date();
        machine = CommonUtil.MACHINE;

        getSourceAndMessage();
        sqlHash = CommonUtil.getHashCode4SQLString(sql);
        this.eventId = eId;
        this.logicDbName = logicDbName;
        this.realDbName = realDbName;
        this.message = message;
        inputParamStr = getInputParameterPrint(parameters);
    }
    
    public void setServerAddress(String serverAddress)
    {
    	this.serverAddress = serverAddress;
    }
    
    public long getDuration() {
		return duration;
	}

	public String getUserName() {
		return userName;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public boolean isTransactional() {
		return transactional;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setDatabaseName(String databaseName)
    {
    	this.databaseName = databaseName;
    }
    
    public void setUserName(String userName)
    {
    	this.userName = userName;
    }
    
    public void setCommandType(DalEventEnum operation)
    {
    	if(DalEventEnum.CALL == operation)
    		this.commandType = "StoreProcedure";
    	else this.commandType = "Text";
    }
    
    public void setTransactional(boolean transactional) {
		this.transactional = transactional;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
	
    public int getResultCount() {
		return resultCount;
	}

	public void setRealDbName(String realDbName) {
		this.realDbName = realDbName;
	}

	public void setLogicDbName(String logicDbName) {
		this.logicDbName = logicDbName;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getDao() {
		return dao;
	}

	public String getMethod() {
		return method;
	}
	
	public int getSqlSize() {
		return sql.length();
	}

	public String getSql() {
		return sql;
	}

	public String getInputParamStr() {
		return inputParamStr;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private static Set<String> execludedClasses;
	static {
		execludedClasses = new HashSet<String>();
		execludedClasses.add(DalDirectClient.class.getName());
		execludedClasses.add(DalTableDao.class.getName());
		execludedClasses.add(DalQueryDao.class.getName());
	}
	
	private void getSourceAndMessage() {
    	StackTraceElement[] callers = Thread.currentThread().getStackTrace();

    	for(int i = 4; i < callers.length; i++) {
    		StackTraceElement caller = callers[i];
        	if(execludedClasses.contains(caller.getClassName()))
        		continue;
        	
        	dao = caller.getClassName();
        	method = caller.getMethodName();
        	source = caller.toString();
//        	source = String.format("%s.%s.%d", dao, method, caller.getLineNumber());
        	break;
    	}
    }

    private String getInputParameterPrint(StatementParameters parameters)
    {
        if (parameters == null) return "";

        StringBuilder sbin = new StringBuilder();
        for (StatementParameter param: parameters.values()) {
        	if(param.isInputParameter()) {
//            	/sbin.append(String.Format("  %s(%d):%s\r\n", para.Name, para.DbType.ToString(), para.isSensitive() ? SQLHIDDENString : m_InputParameters[para.Name]);
        		String paramName = param.getIndex() > 0 ? String.valueOf(param.getIndex()) : param.getName();
    			sbin.append(String.format("  %s(%d):%s\r\n", paramName, param.getSqlType(), param.isSensitive() ? SQLHIDDENString : param.getValue()));
            }
        }

        if (sbin.length() != 0 && Logger.encryptIn) {
            return CommonUtil.desEncrypt(sbin.toString());
        } else {
            return sbin.toString();
        }
    }

    /**
     * To be called after execute Sp
     * @param parameters
     */
    public void setOutputParameters(StatementParameters parameters)
    {
        StringBuilder sbout = new StringBuilder();
        for (StatementParameter param: parameters.values()) {
        	if(param.isOutParameter())
            	sbout.append(String.format("  %s(%d):%s\r\n", param.getName(), param.getSqlType(), param.isSensitive() ? SQLHIDDENString : param.getValue()));
        }
    	
        outputParamStr = sbout.toString();
        
        if (outputParamStr.length() > 0 && Logger.encryptOut) {
        	outputParamStr = CommonUtil.desEncrypt(outputParamStr);
        }
    }

    private String getName() {
    	return logicDbName + '.' + realDbName;
    }
    
    public String ToString()
    {
    	StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append(String.format("Log Name:%s\r\n", getName()));
        sb.append(String.format("Log Source:%s\r\n", source));
        sb.append(String.format("Level:%s\r\n", level));
        sb.append(String.format("DateTime:%s\r\n", timeStamp.toString()));
        sb.append(String.format("Event:%d\r\n", eventId));
        sb.append(String.format("Machine:%s\r\n", machine));
        sb.append(String.format("Message:%s\r\n", message));
        if (sql != null)
        {
            sb.append(String.format("Duration:%d\r\n", duration));
            sb.append(String.format("SQL Text:%s\r\n", sensitive? SQLHIDDENString : sql));
            sb.append(String.format("SQL Hash:%s\r\n", sqlHash));
            sb.append("Input Parameters:").append(inputParamStr).append("\r\n");
            sb.append("Output Parameters:").append(outputParamStr).append("\r\n");
        }
        sb.append('\n');
        return sb.toString();
    }

    /**
     * 获取LogEntry字符串概要表示，用于Central Logging等本身已生成date, appid, machine, level等字段的日志工具
     */
    public String toBrief()
    {
    	StringBuilder sb = new StringBuilder();
        if (sql != null)
        {
            sb.append(String.format("Event:%d\r\n", eventId));
            sb.append(String.format("Message:%s\r\n", message));
            sb.append(String.format("SQL Text:%s\r\n", sensitive? SQLHIDDENString : sql));
            sb.append("Input Parameters:").append(inputParamStr).append("\r\n");
            sb.append("Output Parameters:").append(outputParamStr).append("\r\n");
        } 
        else {
            sb.append(String.format("Log Name:%s\r\n", getName()));
            sb.append(String.format("Log Source:%s\r\n", source));
            sb.append(String.format("Event:%d\r\n", eventId));
            sb.append(String.format("Message:%s\r\n", message));
        }
        sb.append('\n');
        return sb.toString();
    }
}
