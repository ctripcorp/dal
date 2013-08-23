package com.ctrip.sysdev.das.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.ctrip.sysdev.das.msg.AvailableType;

public class SQLRequestHandler extends AbstractRequestHandler {

	public ResultSet fetch(String tnxCtxt, String statement, int flag,
			List<AvailableType> params) throws Exception {

		PreparedStatement ps = connection.prepareStatement(statement);

		for (int i = 0; i < params.size(); i++) {
			params.get(i).setPreparedStatement(ps);
		}

		return ps.executeQuery();

	}

	public int execute(String tnxCtxt, String statement, int flag,
			List<List<AvailableType>> params) throws Exception {
		
		//TODO: add batch operation
		
		List<AvailableType> param = params.get(0);
		
		PreparedStatement ps = connection.prepareStatement(statement);

		for (int i = 0; i < params.size(); i++) {
			param.get(i).setPreparedStatement(ps);
		}

		return ps.executeUpdate(); 
	}

	
}
