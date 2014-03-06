package com.ctrip.platform.dal.dao.logging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.StatementParameter;

public class LogEntry {
    private static String SQLHIDDENString = "*";
    //DateTime
    private Date timeStamp;
    private String machine;
    private Map<Integer, Object> inputParameters = new HashMap<Integer, Object>();
    
    private String name;
    private String source;
    private LogLevel level;
    private long duration;
    private int eventID;
    private String message;


    private Statement statement;

    public LogEntry()
    {
        timeStamp = new Date();
        machine = CommonUtil.getMachinName();
    }

    public LogEntry(Statement statement, String name, int eID, String message)
    {
        this();

        this.statement = statement;
        this.level = LogLevel.Info;
//        this.source = statement.getDao() + "." + statement.getMethod();
        // Is there any performance concern for the following?
        // We should first check if we need to log before actually log
        StackTraceElement caller = Thread.currentThread().getStackTrace()[1];
        this.source = String.format("%s.%s.%d", caller.getClassName(), caller.getMethodName(), caller.getLineNumber());
        
        this.eventID = eID;
        this.name = name;
        this.message = message;

        // In case the parameter get changed by execution sql
		for (StatementParameter parameter: statement.getParams().values()) {
			if(parameter.isInputParameter()) {
				// shall we use name?
				inputParameters.put(parameter.getIndex(), parameter.getValue());
			}
		}
    }

    private String getInputParameterPrint()
    {
        if (statement == null) return "";

        StringBuilder sbin = new StringBuilder();
        for (StatementParameter para: statement.getParams().values()) {
        	if(para.isInputParameter()) {
//            	/sbin.append(String.Format("  %s(%d):%s\r\n", para.Name, para.DbType.ToString(), para.isSensitive() ? SQLHIDDENString : m_InputParameters[para.Name]);
                sbin.append(String.format("  %s(%d):%s\r\n", para.getIndex(), para.getSqlType(), para.isSensitive() ? SQLHIDDENString : inputParameters.get(para.getIndex())));
            }
        }

        if (sbin.length() != 0 && Logger.encryptIn) {
            return CommonUtil.DesEncrypt(sbin.toString());
        } else {
            return sbin.toString();
        }
    }

    private String getOutputParameterPrint()
    {
        if (statement == null) return "";
        StringBuilder sbout = new StringBuilder();
        for (StatementParameter para: statement.getParams().values()) {
            if (para.isOutParameter()) {
            	sbout.append(String.format("  %s(%d):%s\r\n", para.getName(), para.getSqlType(), para.isSensitive() ? SQLHIDDENString : para.getValue()));
            }
        }
        if (sbout.length() != 0 && Logger.encryptOut) {
            return CommonUtil.DesEncrypt(sbout.toString());
        } else {
            return sbout.toString();
        }
    }

    public String ToString()
    {
    	StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append(String.format("Log Name:{0}\r\n", name));
        sb.append(String.format("Log Source:{0}\r\n", source));
        sb.append(String.format("Level:{0}\r\n", level));
        sb.append(String.format("DateTime:{0}\r\n", timeStamp));
        sb.append(String.format("Event:{0}\r\n", eventID));
        sb.append(String.format("Machine:{0}\r\n", machine));
        sb.append(String.format("Message:{0}\r\n", message));
        if (statement != null)
        {
            sb.append(String.format("Duration:{0}\r\n", statement.getDuration()));
            sb.append(String.format("SQL Text:{0}\r\n", statement.isSensitive()? SQLHIDDENString : statement.getSqlText()));
            sb.append(String.format("SQL Hash:{0}\r\n", statement.getSqlHash()));
            sb.append("Input Parameters:").append(getInputParameterPrint()).append("\r\n");
            sb.append("Output Parameters:").append(getOutputParameterPrint()).append("\r\n");
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
        if (statement != null)
        {
            sb.append(String.format("Event:{0}\r\n", eventID));
            sb.append(String.format("Message:{0}\r\n", message));
            sb.append(String.format("SQL Text:{0}\r\n", statement.isSensitive()? SQLHIDDENString : statement.getSqlText()));
            sb.append("Input Parameters:").append(getInputParameterPrint()).append("\r\n");
            sb.append("Output Parameters:").append(getOutputParameterPrint()).append("\r\n");
        } 
        else {
            sb.append(String.format("Log Name:{0}\r\n", name));
            sb.append(String.format("Log Source:{0}\r\n", source));
            sb.append(String.format("Event:{0}\r\n", eventID));
            sb.append(String.format("Message:{0}\r\n", message));
        }
        sb.append('\n');
        return sb.toString();
    }
}
