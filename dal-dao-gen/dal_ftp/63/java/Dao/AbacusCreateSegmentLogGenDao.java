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

public class AbacusCreateSegmentLogGenDao {
	private static final String DATA_BASE = "AbacusDB_INSERT_1";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from AbacusCreateSegmentLog";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM AbacusCreateSegmentLog";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by LogID desc ) as rownum" 
			+" from AbacusCreateSegmentLog (nolock)) select * from CTE where rownum between %s and %s";

	private static final String INSERT_SP_NAME = "spA_AbacusCreateSegmentLog_i";
	private static final String DELETE_SP_NAME = "spA_AbacusCreateSegmentLog_d";
	private static final String UPDATE_SP_NAME = "spA_AbacusCreateSegmentLog_u";
	private static final String RET_CODE = "retcode";
	private static final String UPDATE_COUNT = "update_count";
	
	private DalParser<AbacusCreateSegmentLogGen> parser = new AbacusCreateSegmentLogGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<AbacusCreateSegmentLogGen> rowextractor = null;
	private DalTableDao<AbacusCreateSegmentLogGen> client;
	private DalClient baseClient;

	public AbacusCreateSegmentLogGenDao() {
		this.client = new DalTableDao<AbacusCreateSegmentLogGen>(parser);
		this.rowextractor = new DalRowMapperExtractor<AbacusCreateSegmentLogGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public AbacusCreateSegmentLogGen queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}

	public AbacusCreateSegmentLogGen queryByPk(AbacusCreateSegmentLogGen pk)
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
	
	public List<AbacusCreateSegmentLogGen> queryByPage(AbacusCreateSegmentLogGen pk, int pageSize, int pageNo)  throws SQLException {
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
	
	public List<AbacusCreateSegmentLogGen> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<AbacusCreateSegmentLogGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	public int insert(AbacusCreateSegmentLogGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(INSERT_SP_NAME, parameters, parser.getFields(daoPojo));

		parameters.registerInOut("@LogID", Types.INTEGER, daoPojo.getLogID());

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

		Integer logID = (Integer)parameters.get("@LogID", ParameterDirection.InputOutput).getValue();
		
		return (Integer)results.get(RET_CODE);
	}

	public int delete(AbacusCreateSegmentLogGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(DELETE_SP_NAME, parameters, parser.getPrimaryKeys(daoPojo));


		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		return (Integer)results.get(RET_CODE);
	}

	public int update(AbacusCreateSegmentLogGen daoPojo) throws SQLException {
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
	
	private static class AbacusCreateSegmentLogGenParser extends AbstractDalParser<AbacusCreateSegmentLogGen> {
		public static final String DATABASE_NAME = "AbacusDB_INSERT_1";
		public static final String TABLE_NAME = "AbacusCreateSegmentLog";
		private static final String[] COLUMNS = new String[]{
			"LogID",
			"UID",
			"OrderID",
			"BeginTime",
			"EndTime",
			"Result",
			"SessionID",
			"FlightSegmentStatus",
			"ErrDesc",
			"Source",
			"InsertTime",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"LogID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.VARCHAR,
			Types.INTEGER,
			Types.TIMESTAMP,
			Types.TIMESTAMP,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.TIMESTAMP,
			Types.TIMESTAMP,
		};
		
		public AbacusCreateSegmentLogGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public AbacusCreateSegmentLogGen map(ResultSet rs, int rowNum) throws SQLException {
			AbacusCreateSegmentLogGen pojo = new AbacusCreateSegmentLogGen();
			
			pojo.setLogID((Integer)rs.getObject("LogID"));
			pojo.setUID((String)rs.getObject("UID"));
			pojo.setOrderID((Integer)rs.getObject("OrderID"));
			pojo.setBeginTime((Timestamp)rs.getObject("BeginTime"));
			pojo.setEndTime((Timestamp)rs.getObject("EndTime"));
			pojo.setResult((Integer)rs.getObject("Result"));
			pojo.setSessionID((String)rs.getObject("SessionID"));
			pojo.setFlightSegmentStatus((String)rs.getObject("FlightSegmentStatus"));
			pojo.setErrDesc((String)rs.getObject("ErrDesc"));
			pojo.setSource((String)rs.getObject("Source"));
			pojo.setInsertTime((Timestamp)rs.getObject("InsertTime"));
			pojo.setDatachangeLasttime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(AbacusCreateSegmentLogGen pojo) {
			return pojo.getLogID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(AbacusCreateSegmentLogGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("LogID", pojo.getLogID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(AbacusCreateSegmentLogGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("LogID", pojo.getLogID());
			map.put("UID", pojo.getUID());
			map.put("OrderID", pojo.getOrderID());
			map.put("BeginTime", pojo.getBeginTime());
			map.put("EndTime", pojo.getEndTime());
			map.put("Result", pojo.getResult());
			map.put("SessionID", pojo.getSessionID());
			map.put("FlightSegmentStatus", pojo.getFlightSegmentStatus());
			map.put("ErrDesc", pojo.getErrDesc());
			map.put("Source", pojo.getSource());
			map.put("InsertTime", pojo.getInsertTime());
			map.put("DataChange_LastTime", pojo.getDatachangeLasttime());
	
			return map;
		}
	}
}