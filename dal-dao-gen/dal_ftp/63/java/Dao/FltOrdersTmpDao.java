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

public class FltOrdersTmpDao {
	private static final String DATA_BASE = "AccCorpDB_INSERT_1";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from _FltOrdersTmp";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM _FltOrdersTmp";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by RecordId desc ) as rownum" 
			+" from _FltOrdersTmp (nolock)) select * from CTE where rownum between %s and %s";

	private static final String INSERT_SP_NAME = "spA__FltOrdersTmp_i";
	private static final String DELETE_SP_NAME = "spA__FltOrdersTmp_d";
	private static final String UPDATE_SP_NAME = "spA__FltOrdersTmp_u";
	private static final String RET_CODE = "retcode";
	private static final String UPDATE_COUNT = "update_count";
	
	private DalParser<FltOrdersTmp> parser = new FltOrdersTmpParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<FltOrdersTmp> rowextractor = null;
	private DalTableDao<FltOrdersTmp> client;
	private DalClient baseClient;

	public FltOrdersTmpDao() {
		this.client = new DalTableDao<FltOrdersTmp>(parser);
		this.rowextractor = new DalRowMapperExtractor<FltOrdersTmp>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public FltOrdersTmp queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}

	public FltOrdersTmp queryByPk(FltOrdersTmp pk)
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
	
	public List<FltOrdersTmp> queryByPage(FltOrdersTmp pk, int pageSize, int pageNo)  throws SQLException {
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
	
	public List<FltOrdersTmp> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<FltOrdersTmp> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	public int insert(FltOrdersTmp daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(INSERT_SP_NAME, parameters, parser.getFields(daoPojo));

		parameters.registerInOut("@RecordId", Types.INTEGER, daoPojo.getRecordId());

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

		Integer recordId = (Integer)parameters.get("@RecordId", ParameterDirection.InputOutput).getValue();
		
		return (Integer)results.get(RET_CODE);
	}

	public int delete(FltOrdersTmp daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(DELETE_SP_NAME, parameters, parser.getPrimaryKeys(daoPojo));


		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		return (Integer)results.get(RET_CODE);
	}

	public int update(FltOrdersTmp daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(UPDATE_SP_NAME, parameters, parser.getFields(daoPojo));


		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		return (Integer)results.get(RET_CODE);
	}

    public List<FltOrdersTmp> ccc(BigDecimal Tax) 
			throws SQLException {
		String sql = "SELECT * FROM _FltOrdersTmp WHERE  Tax = ? ";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
		parameters.set(i++, Types.DECIMAL, Tax);
		return queryDao.query(sql, parameters, hints, personRowMapper);
	}

	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		parameters.setResultsParameter(UPDATE_COUNT);
		return callSql;
	}
	
	private static class FltOrdersTmpParser extends AbstractDalParser<FltOrdersTmp> {
		public static final String DATABASE_NAME = "AccCorpDB_INSERT_1";
		public static final String TABLE_NAME = "_FltOrdersTmp";
		private static final String[] COLUMNS = new String[]{
			"RecordId",
			"OrderId",
			"PassengerName",
			"Sequence",
			"AccCheckId",
			"Price",
			"Tax",
			"OilFee",
			"Sendticketfee",
			"Insurancefee",
			"ServiceFee",
			"Refund",
			"delAdjustAmount",
			"AdjustedAmount",
			"OrderStatus",
			"Remark",
			"CreateTime",
			"ConfirmTime",
			"DailyConfirmFlag",
			"DealID",
			"Cost",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"RecordId",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.SMALLINT,
			Types.INTEGER,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.CHAR,
			Types.VARCHAR,
			Types.TIMESTAMP,
			Types.TIMESTAMP,
			Types.CHAR,
			Types.INTEGER,
			Types.DECIMAL,
			Types.TIMESTAMP,
		};
		
		public FltOrdersTmpParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public FltOrdersTmp map(ResultSet rs, int rowNum) throws SQLException {
			FltOrdersTmp pojo = new FltOrdersTmp();
			
			pojo.setRecordId((Integer)rs.getObject("RecordId"));
			pojo.setOrderId((Integer)rs.getObject("OrderId"));
			pojo.setPassengerName((String)rs.getObject("PassengerName"));
			pojo.setSequence((Integer)rs.getObject("Sequence"));
			pojo.setAccCheckId((Integer)rs.getObject("AccCheckId"));
			pojo.setPrice((BigDecimal)rs.getObject("Price"));
			pojo.setTax((BigDecimal)rs.getObject("Tax"));
			pojo.setOilFee((BigDecimal)rs.getObject("OilFee"));
			pojo.setSendticketfee((BigDecimal)rs.getObject("Sendticketfee"));
			pojo.setInsurancefee((BigDecimal)rs.getObject("Insurancefee"));
			pojo.setServiceFee((BigDecimal)rs.getObject("ServiceFee"));
			pojo.setRefund((BigDecimal)rs.getObject("Refund"));
			pojo.setDelAdjustAmount((BigDecimal)rs.getObject("delAdjustAmount"));
			pojo.setAdjustedAmount((BigDecimal)rs.getObject("AdjustedAmount"));
			pojo.setOrderStatus((String)rs.getObject("OrderStatus"));
			pojo.setRemark((String)rs.getObject("Remark"));
			pojo.setCreateTime((Timestamp)rs.getObject("CreateTime"));
			pojo.setConfirmTime((Timestamp)rs.getObject("ConfirmTime"));
			pojo.setDailyConfirmFlag((String)rs.getObject("DailyConfirmFlag"));
			pojo.setDealID((Integer)rs.getObject("DealID"));
			pojo.setCost((BigDecimal)rs.getObject("Cost"));
			pojo.setDatachangeLasttime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(FltOrdersTmp pojo) {
			return pojo.getRecordId();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(FltOrdersTmp pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("RecordId", pojo.getRecordId());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(FltOrdersTmp pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("RecordId", pojo.getRecordId());
			map.put("OrderId", pojo.getOrderId());
			map.put("PassengerName", pojo.getPassengerName());
			map.put("Sequence", pojo.getSequence());
			map.put("AccCheckId", pojo.getAccCheckId());
			map.put("Price", pojo.getPrice());
			map.put("Tax", pojo.getTax());
			map.put("OilFee", pojo.getOilFee());
			map.put("Sendticketfee", pojo.getSendticketfee());
			map.put("Insurancefee", pojo.getInsurancefee());
			map.put("ServiceFee", pojo.getServiceFee());
			map.put("Refund", pojo.getRefund());
			map.put("delAdjustAmount", pojo.getDelAdjustAmount());
			map.put("AdjustedAmount", pojo.getAdjustedAmount());
			map.put("OrderStatus", pojo.getOrderStatus());
			map.put("Remark", pojo.getRemark());
			map.put("CreateTime", pojo.getCreateTime());
			map.put("ConfirmTime", pojo.getConfirmTime());
			map.put("DailyConfirmFlag", pojo.getDailyConfirmFlag());
			map.put("DealID", pojo.getDealID());
			map.put("Cost", pojo.getCost());
			map.put("DataChange_LastTime", pojo.getDatachangeLasttime());
	
			return map;
		}
	}
}