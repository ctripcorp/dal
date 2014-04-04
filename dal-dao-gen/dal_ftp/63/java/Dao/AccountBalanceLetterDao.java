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

public class AccountBalanceLetterDao {
	private static final String DATA_BASE = "AccCorpDB_INSERT_1";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from AccountBalanceLetter";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM AccountBalanceLetter";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by RecordId desc ) as rownum" 
			+" from AccountBalanceLetter (nolock)) select * from CTE where rownum between %s and %s";

	private static final String INSERT_SP_NAME = "spA_AccountBalanceLetter_i";
	private static final String DELETE_SP_NAME = "spA_AccountBalanceLetter_d";
	private static final String UPDATE_SP_NAME = "spA_AccountBalanceLetter_u";
	private static final String RET_CODE = "retcode";
	private static final String UPDATE_COUNT = "update_count";
	
	private DalParser<AccountBalanceLetter> parser = new AccountBalanceLetterParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<AccountBalanceLetter> rowextractor = null;
	private DalTableDao<AccountBalanceLetter> client;
	private DalClient baseClient;

	public AccountBalanceLetterDao() {
		this.client = new DalTableDao<AccountBalanceLetter>(parser);
		this.rowextractor = new DalRowMapperExtractor<AccountBalanceLetter>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public AccountBalanceLetter queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}

	public AccountBalanceLetter queryByPk(AccountBalanceLetter pk)
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
	
	public List<AccountBalanceLetter> queryByPage(AccountBalanceLetter pk, int pageSize, int pageNo)  throws SQLException {
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
	
	public List<AccountBalanceLetter> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<AccountBalanceLetter> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	public int insert(AccountBalanceLetter daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(INSERT_SP_NAME, parameters, parser.getFields(daoPojo));

		parameters.registerInOut("@RecordId", Types.INTEGER, daoPojo.getRecordId());

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

		Integer recordId = (Integer)parameters.get("@RecordId", ParameterDirection.InputOutput).getValue();
		
		return (Integer)results.get(RET_CODE);
	}

	public int delete(AccountBalanceLetter daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(DELETE_SP_NAME, parameters, parser.getPrimaryKeys(daoPojo));


		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		return (Integer)results.get(RET_CODE);
	}

	public int update(AccountBalanceLetter daoPojo) throws SQLException {
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
	
	private static class AccountBalanceLetterParser extends AbstractDalParser<AccountBalanceLetter> {
		public static final String DATABASE_NAME = "AccCorpDB_INSERT_1";
		public static final String TABLE_NAME = "AccountBalanceLetter";
		private static final String[] COLUMNS = new String[]{
			"RecordId",
			"AccBalanceId",
			"FileName",
			"UploadPerson",
			"DescribeInfo",
			"CreateTime",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"RecordId",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.TIMESTAMP,
			Types.TIMESTAMP,
		};
		
		public AccountBalanceLetterParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public AccountBalanceLetter map(ResultSet rs, int rowNum) throws SQLException {
			AccountBalanceLetter pojo = new AccountBalanceLetter();
			
			pojo.setRecordId((Integer)rs.getObject("RecordId"));
			pojo.setAccBalanceId((Integer)rs.getObject("AccBalanceId"));
			pojo.setFileName((String)rs.getObject("FileName"));
			pojo.setUploadPerson((String)rs.getObject("UploadPerson"));
			pojo.setDescribeInfo((String)rs.getObject("DescribeInfo"));
			pojo.setCreateTime((Timestamp)rs.getObject("CreateTime"));
			pojo.setDatachangeLasttime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(AccountBalanceLetter pojo) {
			return pojo.getRecordId();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(AccountBalanceLetter pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("RecordId", pojo.getRecordId());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(AccountBalanceLetter pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("RecordId", pojo.getRecordId());
			map.put("AccBalanceId", pojo.getAccBalanceId());
			map.put("FileName", pojo.getFileName());
			map.put("UploadPerson", pojo.getUploadPerson());
			map.put("DescribeInfo", pojo.getDescribeInfo());
			map.put("CreateTime", pojo.getCreateTime());
			map.put("DataChange_LastTime", pojo.getDatachangeLasttime());
	
			return map;
		}
	}
}