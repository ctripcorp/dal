package com.ctrip.dal.test.test4;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;


import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class AccCorpDB_INSERT_1SpDao {
	private static final String DATA_BASE = "AccCorpDB_INSERT_1";
	private DalClient client;

	public AccCorpDB_INSERT_1SpDao() {
		this.client = DalClientFactory.getClient(DATA_BASE);
	}
	
	public Map<String, ?> callGetret(Getret param) throws SQLException {
		String callString = "CN1\jian_chen.getret(?,?)";
		StatementParameters parameters = new StatementParameters();
		
		parameters.registerInOut("@para1", Types.INTEGER, param.getPara1());
		parameters.registerInOut("@para2", Types.INTEGER, param.getPara2());

		/* To specify returned result(not the output or inputoutput parameter)
		DalRowMapperExtractor<Map<String, Object>> extractor = new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper());
		param = StatementParameter.newBuilder().setResultsParameter(true).setResultSetExtractor(extractor).setName("result").build();
		parameters.add(param);

		param  = StatementParameter.newBuilder().setResultsParameter(true).setName("count").build();
		parameters.add(param);
		*/
		
		DalHints hints = new DalHints();
		
		return client.call(callString, parameters, hints);
	
	}
	
	public Map<String, ?> callTestSearchTable(TestSearchTable param) throws SQLException {
		String callString = "CN1\jian_chen.Test_SearchTable()";
		StatementParameters parameters = new StatementParameters();
		

		/* To specify returned result(not the output or inputoutput parameter)
		DalRowMapperExtractor<Map<String, Object>> extractor = new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper());
		param = StatementParameter.newBuilder().setResultsParameter(true).setResultSetExtractor(extractor).setName("result").build();
		parameters.add(param);

		param  = StatementParameter.newBuilder().setResultsParameter(true).setName("count").build();
		parameters.add(param);
		*/
		
		DalHints hints = new DalHints();
		
		return client.call(callString, parameters, hints);
	
	}
	

}