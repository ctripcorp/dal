package com.ctrip.platform.dao.client;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dao.param.StatementParameter;

public class DasClient implements Client {

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
