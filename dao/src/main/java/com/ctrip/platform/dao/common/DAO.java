package com.ctrip.platform.dao.common;

import java.sql.ResultSet;
import java.util.List;

import com.ctrip.platform.dao.param.Parameter;

public interface DAO {
	
	/**
	 * 
	 * @param tnxCtxt
	 * @param statement
	 * @param params
	 * @param flag
	 * @return
	 */
	public ResultSet fetch(String tnxCtxt, int flag, String statement, 
			Parameter... params) throws Exception;
	
	public <T> List<T> fetchVO(String tnxCtxt, int flag, String statement, 
			Parameter... params) throws Exception;
	
	public ResultSet fetchBySp(String tnxCtxt, int flag, String sp, 
			Parameter... params) throws Exception;
	
	public <T> List<T> fetchVOBySp(String tnxCtxt, int flag, String sp, 
			Parameter... params) throws Exception;
	
	/**
	 * Want to implement Bulk insert?
	 * @param tnxCtxt
	 * @param statement
	 * @param params
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public int execute(String tnxCtxt, int flag, String statement, 
			Parameter... params) throws Exception;
	
	public int executeSp(String tnxCtxt, int flag, String sp, 
			Parameter... params) throws Exception;
}
