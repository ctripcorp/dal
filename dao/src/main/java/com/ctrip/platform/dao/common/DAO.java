package com.ctrip.platform.dao.common;

import java.sql.ResultSet;
import java.util.List;

import com.ctrip.platform.dao.param.Parameter;

public interface DAO {
	
	public ResultSet fetch(String statement, Parameter... params);
	
	public <T> List<T> fetch();
	
	public int insert(String statement, Parameter... params);
	
	public int update(String statement, Parameter... params);
	
	public int delete(String statement, Parameter... params);
	
	public ResultSet fetchBySp(String spName, Parameter... params);
	
	public int executeSp(String spName, Parameter... params);
	
//	public ResultSet fetch(String tnxCtxt, int flag, String statement, 
//			Parameter... params) throws Exception;
//	
//	public <T> List<T> fetchVO(String tnxCtxt, int flag, String statement, 
//			Parameter... params) throws Exception;
//	
//	public ResultSet fetchBySp(String tnxCtxt, int flag, String sp, 
//			Parameter... params) throws Exception;
//	
//	public <T> List<T> fetchVOBySp(String tnxCtxt, int flag, String sp, 
//			Parameter... params) throws Exception;
//	
//	public int execute(String tnxCtxt, int flag, String statement, 
//			Parameter... params) throws Exception;
//	
//	public int executeSp(String tnxCtxt, int flag, String sp, 
//			Parameter... params) throws Exception;
}
