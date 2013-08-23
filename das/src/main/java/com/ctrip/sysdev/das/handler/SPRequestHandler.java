package com.ctrip.sysdev.das.handler;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.List;

import com.ctrip.sysdev.das.msg.AvailableType;

public class SPRequestHandler extends AbstractRequestHandler{
	
	public ResultSet fetchBySp(String tnxCtxt, String sp, int flag,
			List<AvailableType> params) throws Exception {

		StringBuffer occupy = new StringBuffer();

		for (int i = 0; i < params.size(); i++) {
			occupy.append("?");
			occupy.append(",");
		}
		occupy.deleteCharAt(occupy.length() - 1);

		CallableStatement callableStmt = connection.prepareCall(String.format(
				"{call dbo.%s(%s)}", sp, occupy.toString()));

		for (int i = 0; i < params.size(); i++) {
			params.get(i).setCallableStatement(callableStmt);
		}

		return callableStmt.executeQuery();
	}

	/**
	 * 
	 * @param tnxCtxt
	 * @param sp
	 * @param flag
	 * @param params
	 * @return
	 */
	public int executeSp(String tnxCtxt, String sp, int flag,
			List<AvailableType> params) throws Exception {
		
		StringBuffer occupy = new StringBuffer();

		for (int i = 0; i < params.size(); i++) {
			occupy.append("?");
			occupy.append(",");
		}
		occupy.deleteCharAt(occupy.length() - 1);

		CallableStatement callableStmt = connection.prepareCall(String.format(
				"{call dbo.%s(%s)}", sp, occupy.toString()));

		for (int i = 0; i < params.size(); i++) {
			params.get(i).setCallableStatement(callableStmt);
		}

		return callableStmt.executeUpdate();
	}

}
