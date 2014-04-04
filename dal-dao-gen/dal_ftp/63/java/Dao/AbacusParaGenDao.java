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

public class AbacusParaGenDao {
	private static final String DATA_BASE = "AbacusDB_INSERT_1";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from AbacusPara";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM AbacusPara";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by ParaID desc ) as rownum" 
			+" from AbacusPara (nolock)) select * from CTE where rownum between %s and %s";

	private static final String INSERT_SP_NAME = "spA_AbacusPara_i";
	private static final String DELETE_SP_NAME = "spA_AbacusPara_d";
	private static final String UPDATE_SP_NAME = "spA_AbacusPara_u";
	private static final String RET_CODE = "retcode";
	private static final String UPDATE_COUNT = "update_count";
	
	private DalParser<AbacusParaGen> parser = new AbacusParaGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<AbacusParaGen> rowextractor = null;
	private DalTableDao<AbacusParaGen> client;
	private DalClient baseClient;

	public AbacusParaGenDao() {
		this.client = new DalTableDao<AbacusParaGen>(parser);
		this.rowextractor = new DalRowMapperExtractor<AbacusParaGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public AbacusParaGen queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}

	public AbacusParaGen queryByPk(AbacusParaGen pk)
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
	
	public List<AbacusParaGen> queryByPage(AbacusParaGen pk, int pageSize, int pageNo)  throws SQLException {
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
	
	public List<AbacusParaGen> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<AbacusParaGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	public int insert(AbacusParaGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(INSERT_SP_NAME, parameters, parser.getFields(daoPojo));


		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

		
		return (Integer)results.get(RET_CODE);
	}

	public int delete(AbacusParaGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(DELETE_SP_NAME, parameters, parser.getPrimaryKeys(daoPojo));


		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		return (Integer)results.get(RET_CODE);
	}

	public int update(AbacusParaGen daoPojo) throws SQLException {
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
	
	private static class AbacusParaGenParser extends AbstractDalParser<AbacusParaGen> {
		public static final String DATABASE_NAME = "AbacusDB_INSERT_1";
		public static final String TABLE_NAME = "AbacusPara";
		private static final String[] COLUMNS = new String[]{
			"ParaID",
			"ParaTypeID",
			"ParaName",
			"ParaValue",
			"Description",
			"AbacusWSID",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"ParaID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.SMALLINT,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.SMALLINT,
			Types.TIMESTAMP,
		};
		
		public AbacusParaGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public AbacusParaGen map(ResultSet rs, int rowNum) throws SQLException {
			AbacusParaGen pojo = new AbacusParaGen();
			
			pojo.setParaID((Integer)rs.getObject("ParaID"));
			pojo.setParaTypeID((Integer)rs.getObject("ParaTypeID"));
			pojo.setParaName((String)rs.getObject("ParaName"));
			pojo.setParaValue((String)rs.getObject("ParaValue"));
			pojo.setDescription((String)rs.getObject("Description"));
			pojo.setAbacusWSID((Integer)rs.getObject("AbacusWSID"));
			pojo.setDatachangeLasttime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return false;
		}
	
		@Override
		public Number getIdentityValue(AbacusParaGen pojo) {
			return null;
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(AbacusParaGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("ParaID", pojo.getParaID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(AbacusParaGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("ParaID", pojo.getParaID());
			map.put("ParaTypeID", pojo.getParaTypeID());
			map.put("ParaName", pojo.getParaName());
			map.put("ParaValue", pojo.getParaValue());
			map.put("Description", pojo.getDescription());
			map.put("AbacusWSID", pojo.getAbacusWSID());
			map.put("DataChange_LastTime", pojo.getDatachangeLasttime());
	
			return map;
		}
	}
}