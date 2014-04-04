package com.ctrip.dal.test.test4;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.AbstractDalParser;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

public class AbacusAirBookLogGenDao {
	private static final String DATA_BASE = "AbacusDB_INSERT_1";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from AbacusAirBookLog";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM AbacusAirBookLog";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by LogID desc ) as rownum" 
			+" from AbacusAirBookLog (nolock)) select * from CTE where rownum between %s and %s";

	private static final String INSERT_SP_NAME = "spA_AbacusAirBookLog_i";
	private static final String DELETE_SP_NAME = "spA_AbacusAirBookLog_d";
	private static final String UPDATE_SP_NAME = "spA_AbacusAirBookLog_u";
	private static final String RET_CODE = "retcode";
	private static final String UPDATE_COUNT = "update_count";
	
	private DalParser<AbacusAirBookLogGen> parser = new AbacusAirBookLogGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<AbacusAirBookLogGen> rowextractor = null;
	private DalTableDao<AbacusAirBookLogGen> client;
	private DalClient baseClient;

	public AbacusAirBookLogGenDao() {
		this.client = new DalTableDao<AbacusAirBookLogGen>(parser);
		this.rowextractor = new DalRowMapperExtractor<AbacusAirBookLogGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public AbacusAirBookLogGen queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}

	public AbacusAirBookLogGen queryByPk(AbacusAirBookLogGen pk)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(pk, hints);
	}
	
	public long count()  throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		long result = (Long)this.baseClient.query(COUNT_SQL_PATTERN, parameters, hints, extractor);
		return result;
	}
	
	public List<AbacusAirBookLogGen> queryByPage(AbacusAirBookLogGen pk, int pageSize, int pageNo)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");
		
        StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String sql = "";
		int fromRownum = (pageNo - 1) * pageSize + 1;
        int endRownum = pageSize * pageNo;
		sql = String.format(PAGE_SQL_PATTERN, fromRownum, endRownum);
		return this.baseClient.query(sql, parameters, hints, rowextractor);
	}
	
	public List<AbacusAirBookLogGen> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<AbacusAirBookLogGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	public int insert(AbacusAirBookLogGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(INSERT_SP_NAME, parameters, parser.getFields(daoPojo));

		parameters.registerInOut("@LogID", Types.INTEGER, daoPojo.getLogID());

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

		Integer logID = (Integer)parameters.get("@LogID", ParameterDirection.InputOutput).getValue();
		
		return (Integer)results.get(RET_CODE);
	}

	public int delete(AbacusAirBookLogGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(DELETE_SP_NAME, parameters, parser.getPrimaryKeys(daoPojo));


		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		return (Integer)results.get(RET_CODE);
	}

	public int update(AbacusAirBookLogGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(UPDATE_SP_NAME, parameters, parser.getFields(daoPojo));


		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		return (Integer)results.get(RET_CODE);
	}


	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		parameters.setResultsParameter(UPDATE_COUNT);
		return callSql;
	}
	
	private static class AbacusAirBookLogGenParser extends AbstractDalParser<AbacusAirBookLogGen> {
		public static final String DATABASE_NAME = "AbacusDB_INSERT_1";
		public static final String TABLE_NAME = "AbacusAirBookLog";
		private static final String[] COLUMNS = new String[]{
			"LogID",
			"ReferenceID",
			"UID",
			"OrderID",
			"BeginTime",
			"EndTime",
			"Result",
			"PNR",
			"ErrDesc",
			"Source",
			"InsertTime",
			"SnNo",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"LogID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.INTEGER,
			Types.TIMESTAMP,
			Types.TIMESTAMP,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.TIMESTAMP,
			Types.VARCHAR,
			Types.TIMESTAMP,
		};
		
		public AbacusAirBookLogGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public AbacusAirBookLogGen map(ResultSet rs, int rowNum) throws SQLException {
			AbacusAirBookLogGen pojo = new AbacusAirBookLogGen();
			
			pojo.setLogID((Integer)rs.getObject("LogID"));
			pojo.setReferenceID((String)rs.getObject("ReferenceID"));
			pojo.setUID((String)rs.getObject("UID"));
			pojo.setOrderID((Integer)rs.getObject("OrderID"));
			pojo.setBeginTime((Timestamp)rs.getObject("BeginTime"));
			pojo.setEndTime((Timestamp)rs.getObject("EndTime"));
			pojo.setResult((Integer)rs.getObject("Result"));
			pojo.setPNR((String)rs.getObject("PNR"));
			pojo.setErrDesc((String)rs.getObject("ErrDesc"));
			pojo.setSource((String)rs.getObject("Source"));
			pojo.setInsertTime((Timestamp)rs.getObject("InsertTime"));
			pojo.setSnNo((String)rs.getObject("SnNo"));
			pojo.setDatachangeLasttime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(AbacusAirBookLogGen pojo) {
			return pojo.getLogID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(AbacusAirBookLogGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("LogID", pojo.getLogID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(AbacusAirBookLogGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("LogID", pojo.getLogID());
			map.put("ReferenceID", pojo.getReferenceID());
			map.put("UID", pojo.getUID());
			map.put("OrderID", pojo.getOrderID());
			map.put("BeginTime", pojo.getBeginTime());
			map.put("EndTime", pojo.getEndTime());
			map.put("Result", pojo.getResult());
			map.put("PNR", pojo.getPNR());
			map.put("ErrDesc", pojo.getErrDesc());
			map.put("Source", pojo.getSource());
			map.put("InsertTime", pojo.getInsertTime());
			map.put("SnNo", pojo.getSnNo());
			map.put("DataChange_LastTime", pojo.getDatachangeLasttime());
	
			return map;
		}
	}
}