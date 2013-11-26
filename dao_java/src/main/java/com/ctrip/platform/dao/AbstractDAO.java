package com.ctrip.platform.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dao.param.StatementParameter;

public class AbstractDAO implements DAO {

	@Override
	public ResultSet fetch(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int execute(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ResultSet fetchBySp(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int executeSp(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
