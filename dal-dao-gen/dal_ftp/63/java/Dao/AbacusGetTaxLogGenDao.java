package com.ctrip.dal.test.test4;

import java.math.BigDecimal;
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

public class AbacusGetTaxLogGenDao {
	private static final String DATA_BASE = "AbacusDB_INSERT_1";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from AbacusGetTaxLog";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM AbacusGetTaxLog";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by LogID desc ) as rownum" 
			+" from AbacusGetTaxLog (nolock)) select * from CTE where rownum between %s and %s";

	private static final String INSERT_SP_NAME = "spA_AbacusGetTaxLog_i";
	private static final String DELETE_SP_NAME = "spA_AbacusGetTaxLog_d";
	private static final String UPDATE_SP_NAME = "spA_AbacusGetTaxLog_u";
	private static final String RET_CODE = "retcode";
	private static final String UPDATE_COUNT = "update_count";
	
	private DalParser<AbacusGetTaxLogGen> parser = new AbacusGetTaxLogGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<AbacusGetTaxLogGen> rowextractor = null;
	private DalTableDao<AbacusGetTaxLogGen> client;
	private DalClient baseClient;

	public AbacusGetTaxLogGenDao() {
		this.client = new DalTableDao<AbacusGetTaxLogGen>(parser);
		this.rowextractor = new DalRowMapperExtractor<AbacusGetTaxLogGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public AbacusGetTaxLogGen queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}

	public AbacusGetTaxLogGen queryByPk(AbacusGetTaxLogGen pk)
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
	
	public List<AbacusGetTaxLogGen> queryByPage(AbacusGetTaxLogGen pk, int pageSize, int pageNo)  throws SQLException {
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
	
	public List<AbacusGetTaxLogGen> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<AbacusGetTaxLogGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	public int insert(AbacusGetTaxLogGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(INSERT_SP_NAME, parameters, parser.getFields(daoPojo));

		parameters.registerInOut("@LogID", Types.INTEGER, daoPojo.getLogID());

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

		Integer logID = (Integer)parameters.get("@LogID", ParameterDirection.InputOutput).getValue();
		
		return (Integer)results.get(RET_CODE);
	}

	public int delete(AbacusGetTaxLogGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(DELETE_SP_NAME, parameters, parser.getPrimaryKeys(daoPojo));


		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		return (Integer)results.get(RET_CODE);
	}

	public int update(AbacusGetTaxLogGen daoPojo) throws SQLException {
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
	
	private static class AbacusGetTaxLogGenParser extends AbstractDalParser<AbacusGetTaxLogGen> {
		public static final String DATABASE_NAME = "AbacusDB_INSERT_1";
		public static final String TABLE_NAME = "AbacusGetTaxLog";
		private static final String[] COLUMNS = new String[]{
			"LogID",
			"UID",
			"OrderID",
			"PNR",
			"BeginTime",
			"EndTime",
			"Result",
			"Tax",
			"ErrDesc",
			"Source",
			"InsertTime",
			"PassengerType",
			"OperatingAirline",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"LogID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.VARCHAR,
			Types.INTEGER,
			Types.VARCHAR,
			Types.TIMESTAMP,
			Types.TIMESTAMP,
			Types.INTEGER,
			Types.DECIMAL,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.TIMESTAMP,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.TIMESTAMP,
		};
		
		public AbacusGetTaxLogGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public AbacusGetTaxLogGen map(ResultSet rs, int rowNum) throws SQLException {
			AbacusGetTaxLogGen pojo = new AbacusGetTaxLogGen();
			
			pojo.setLogID((Integer)rs.getObject("LogID"));
			pojo.setUID((String)rs.getObject("UID"));
			pojo.setOrderID((Integer)rs.getObject("OrderID"));
			pojo.setPNR((String)rs.getObject("PNR"));
			pojo.setBeginTime((Timestamp)rs.getObject("BeginTime"));
			pojo.setEndTime((Timestamp)rs.getObject("EndTime"));
			pojo.setResult((Integer)rs.getObject("Result"));
			pojo.setTax((BigDecimal)rs.getObject("Tax"));
			pojo.setErrDesc((String)rs.getObject("ErrDesc"));
			pojo.setSource((String)rs.getObject("Source"));
			pojo.setInsertTime((Timestamp)rs.getObject("InsertTime"));
			pojo.setPassengerType((String)rs.getObject("PassengerType"));
			pojo.setOperatingAirline((String)rs.getObject("OperatingAirline"));
			pojo.setDatachangeLasttime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(AbacusGetTaxLogGen pojo) {
			return pojo.getLogID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(AbacusGetTaxLogGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("LogID", pojo.getLogID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(AbacusGetTaxLogGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("LogID", pojo.getLogID());
			map.put("UID", pojo.getUID());
			map.put("OrderID", pojo.getOrderID());
			map.put("PNR", pojo.getPNR());
			map.put("BeginTime", pojo.getBeginTime());
			map.put("EndTime", pojo.getEndTime());
			map.put("Result", pojo.getResult());
			map.put("Tax", pojo.getTax());
			map.put("ErrDesc", pojo.getErrDesc());
			map.put("Source", pojo.getSource());
			map.put("InsertTime", pojo.getInsertTime());
			map.put("PassengerType", pojo.getPassengerType());
			map.put("OperatingAirline", pojo.getOperatingAirline());
			map.put("DataChange_LastTime", pojo.getDatachangeLasttime());
	
			return map;
		}
	}
}