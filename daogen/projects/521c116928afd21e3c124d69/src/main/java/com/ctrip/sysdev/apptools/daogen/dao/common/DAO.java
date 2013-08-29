package com.ctrip.sysdev.apptools.daogen.dao.common;

import java.sql.ResultSet;
import java.util.List;

import com.ctrip.sysdev.apptools.daogen.dao.msg.AvailableType;

public interface DAO {
	
	/**
	 * 
	 * @param tnxCtxt
	 * @param statement
	 * @param params
	 * @param flag
	 * @return
	 */
	public ResultSet fetch(String tnxCtxt, String statement, 
			List<AvailableType> params, int flag) throws Exception;
	
	public <T> List<T> fetchByORM(String tnxCtxt, String statement, 
			List<AvailableType> params, int flag) throws Exception;
	
	public int bulkInsert(String tnxCtxt, String statement,
			List<AvailableType> params, int flag) throws Exception;
	
	
	
	public int execute(String tnxCtxt, String statement, 
			List<AvailableType> params, int flag) throws Exception;
	
	public ResultSet fetchBySp(String tnxCtxt, String sp, 
			List<AvailableType> params, int flag) throws Exception;
	
	public <T> List<T> fetchBySpByORM(String tnxCtxt, String sp, 
			List<AvailableType> params, int flag) throws Exception;
	
	public int executeSp(String tnxCtxt, String sp, 
			List<AvailableType> params, int flag) throws Exception;
}
