package com.ctrip.sysdev.dao;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import com.ctrip.sysdev.msg.AvailableType;

public interface IDAO {
	
	/**
	 * 
	 * @param tnxCtxt
	 * @param statement
	 * @param params
	 * @param flag
	 * @return
	 */
	public ResultSet fetch(String tnxCtxt, DAOFunction statement, 
			List<AvailableType> params, int flag) throws Exception;
	
	public <T> List<T> fetchByORM(String tnxCtxt, DAOFunction statement, 
			List<AvailableType> params, int flag) throws Exception;
	
	public int bulkInsert(String tnxCtxt, DAOFunction statement,
			List<AvailableType> params, int flag) throws Exception;
	
	public int execute(String tnxCtxt, DAOFunction statement, 
			List<AvailableType> params, int flag) throws Exception;
	
	public ResultSet fetchBySp(String tnxCtxt, DAOFunction sp, 
			List<AvailableType> params, int flag) throws Exception;
	
	public <T> List<T> fetchBySpByORM(String tnxCtxt, DAOFunction sp, 
			List<AvailableType> params, int flag) throws Exception;
	
	public int executeSp(String tnxCtxt, DAOFunction sp, 
			List<AvailableType> params, int flag) throws Exception;
}
