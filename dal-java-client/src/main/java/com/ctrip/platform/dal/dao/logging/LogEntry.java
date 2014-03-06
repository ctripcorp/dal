package com.ctrip.platform.dal.dao.logging;

import java.util.Date;

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
    private String source;
    private int eventId;
    private String message;
    private String level;
    private long duration;

    public LogEntry()
    {
        timeStamp = new Date();
        machine = CommonUtil.MACHINE;
    }

    /**
     * 
     * @param sql
     * @param parameters
     * @param dbName logic db name and real db name.
     * @param eId
     * @param message
     */
    public LogEntry(String sql, StatementParameters parameters, String logicDbName, int eId)
    {
        this();
        
        // Don't waste time for the rest
        if(!Logger.validate(sql, parameters))
        	return;

        getSourceAndMessage();
        sqlHash = CommonUtil.getHashCode4SQLString(sql);
        this.eventId = eId;
        this.logicDbName = logicDbName;

        inputParamStr = getInputParameterPrint(parameters);
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

	private void getSourceAndMessage() {
    	StackTraceElement[] callers = Thread.currentThread().getStackTrace();
    	for(StackTraceElement caller: callers) {
    		// Message
    		if(message == null && caller.getClassName().endsWith(DalDirectClient.class.getSimpleName())){
    			message = caller.getMethodName();
    			continue;
    		}
    		
        	if(caller.getClassName().equalsIgnoreCase(DalTableDao.class.getName()) || caller.getClassName().equalsIgnoreCase(DalQueryDao.class.getName()))
        		continue;
        	
        	source = String.format("%s.%s.%d", caller.getClassName(), caller.getMethodName(), caller.getLineNumber());
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
        sb.append(String.format("Log Name:{0}\r\n", getName()));
        sb.append(String.format("Log Source:{0}\r\n", source));
        sb.append(String.format("Level:{0}\r\n", level));
        sb.append(String.format("DateTime:{0}\r\n", timeStamp));
        sb.append(String.format("Event:{0}\r\n", eventId));
        sb.append(String.format("Machine:{0}\r\n", machine));
        sb.append(String.format("Message:{0}\r\n", message));
        if (sql != null)
        {
            sb.append(String.format("Duration:%d\r\n", duration));
            sb.append(String.format("SQL Text:%s\r\n", sensitive? SQLHIDDENString : sql));
            sb.append(String.format("SQL Hash:{0}\r\n", sqlHash));
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
            sb.append(String.format("Event:{0}\r\n", eventId));
            sb.append(String.format("Message:{0}\r\n", message));
            sb.append(String.format("SQL Text:{0}\r\n", sensitive? SQLHIDDENString : sql));
            sb.append("Input Parameters:").append(inputParamStr).append("\r\n");
            sb.append("Output Parameters:").append(outputParamStr).append("\r\n");
        } 
        else {
            sb.append(String.format("Log Name:{0}\r\n", getName()));
            sb.append(String.format("Log Source:{0}\r\n", source));
            sb.append(String.format("Event:{0}\r\n", eventId));
            sb.append(String.format("Message:{0}\r\n", message));
        }
        sb.append('\n');
        return sb.toString();
    }
}
