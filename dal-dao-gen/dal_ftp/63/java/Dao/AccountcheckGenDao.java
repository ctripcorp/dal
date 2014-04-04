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

public class AccountcheckGenDao {
	private static final String DATA_BASE = "AccCorpDB_SELECT_1";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from _accountcheck";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM _accountcheck";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by AccCheckID desc ) as rownum" 
			+" from _accountcheck (nolock)) select * from CTE where rownum between %s and %s";
	
	private DalParser<AccountcheckGen> parser = new AccountcheckGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<AccountcheckGen> rowextractor = null;
	private DalTableDao<AccountcheckGen> client;
	private DalClient baseClient;

	public AccountcheckGenDao() {
		this.client = new DalTableDao<AccountcheckGen>(parser);
		this.rowextractor = new DalRowMapperExtractor<AccountcheckGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public AccountcheckGen queryByPk()
			throws SQLException {
		DalHints hints = new DalHints();
		AccountcheckGen pk = new AccountcheckGen();
			

		return client.queryByPk(pk, hints);
	}

	public AccountcheckGen queryByPk(AccountcheckGen pk)
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
	
	public List<AccountcheckGen> queryByPage(AccountcheckGen pk, int pageSize, int pageNo)  throws SQLException {
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
	
	public List<AccountcheckGen> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<AccountcheckGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	public void insert(AccountcheckGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.insert(hints, null, daoPojos);
	}

	public void insert(KeyHolder keyHolder, AccountcheckGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.insert(hints, keyHolder, daoPojos);
	}

	public void delete(AccountcheckGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.delete(hints, daoPojos);
	}

	public void update(AccountcheckGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.update(hints, daoPojos);
	}

    public List<AccountcheckGen> fffff(BigDecimal limited) 
			throws SQLException {
		String sql = "SELECT * FROM _accountcheck WHERE  limited != ? ";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
		parameters.set(i++, Types.DECIMAL, limited);
		return queryDao.query(sql, parameters, hints, personRowMapper);
	}

	private static class AccountcheckGenParser extends AbstractDalParser<AccountcheckGen> {
		public static final String DATABASE_NAME = "AccCorpDB_SELECT_1";
		public static final String TABLE_NAME = "_accountcheck";
		private static final String[] COLUMNS = new String[]{
			"AccCheckID",
			"CorpID",
			"BatchNo",
			"AccountID",
			"SubAccountID",
			"BatchStatus",
			"AccBalanceID",
			"AccountType",
			"CheckAccType",
			"Operator",
			"ModifyTime",
			"StartDate",
			"EndDate",
			"FltconMoney",
			"HtlHconMoney",
			"HtlXconMoney",
			"limited",
			"LimitedTemp",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.INTEGER,
			Types.INTEGER,
			Types.CHAR,
			Types.INTEGER,
			Types.CHAR,
			Types.INTEGER,
			Types.VARCHAR,
			Types.TIMESTAMP,
			Types.CHAR,
			Types.CHAR,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.DECIMAL,
			Types.TIMESTAMP,
		};
		
		public AccountcheckGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public AccountcheckGen map(ResultSet rs, int rowNum) throws SQLException {
			AccountcheckGen pojo = new AccountcheckGen();
			
			pojo.setAccCheckID((Integer)rs.getObject("AccCheckID"));
			pojo.setCorpID((String)rs.getObject("CorpID"));
			pojo.setBatchNo((String)rs.getObject("BatchNo"));
			pojo.setAccountID((Integer)rs.getObject("AccountID"));
			pojo.setSubAccountID((Integer)rs.getObject("SubAccountID"));
			pojo.setBatchStatus((String)rs.getObject("BatchStatus"));
			pojo.setAccBalanceID((Integer)rs.getObject("AccBalanceID"));
			pojo.setAccountType((String)rs.getObject("AccountType"));
			pojo.setCheckAccType((Integer)rs.getObject("CheckAccType"));
			pojo.setOperator((String)rs.getObject("Operator"));
			pojo.setModifyTime((Timestamp)rs.getObject("ModifyTime"));
			pojo.setStartDate((String)rs.getObject("StartDate"));
			pojo.setEndDate((String)rs.getObject("EndDate"));
			pojo.setFltconMoney((BigDecimal)rs.getObject("FltconMoney"));
			pojo.setHtlHconMoney((BigDecimal)rs.getObject("HtlHconMoney"));
			pojo.setHtlXconMoney((BigDecimal)rs.getObject("HtlXconMoney"));
			pojo.setLimited((BigDecimal)rs.getObject("limited"));
			pojo.setLimitedTemp((BigDecimal)rs.getObject("LimitedTemp"));
			pojo.setDatachangeLasttime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(AccountcheckGen pojo) {
			return pojo.getAccCheckID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(AccountcheckGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(AccountcheckGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("AccCheckID", pojo.getAccCheckID());
			map.put("CorpID", pojo.getCorpID());
			map.put("BatchNo", pojo.getBatchNo());
			map.put("AccountID", pojo.getAccountID());
			map.put("SubAccountID", pojo.getSubAccountID());
			map.put("BatchStatus", pojo.getBatchStatus());
			map.put("AccBalanceID", pojo.getAccBalanceID());
			map.put("AccountType", pojo.getAccountType());
			map.put("CheckAccType", pojo.getCheckAccType());
			map.put("Operator", pojo.getOperator());
			map.put("ModifyTime", pojo.getModifyTime());
			map.put("StartDate", pojo.getStartDate());
			map.put("EndDate", pojo.getEndDate());
			map.put("FltconMoney", pojo.getFltconMoney());
			map.put("HtlHconMoney", pojo.getHtlHconMoney());
			map.put("HtlXconMoney", pojo.getHtlXconMoney());
			map.put("limited", pojo.getLimited());
			map.put("LimitedTemp", pojo.getLimitedTemp());
			map.put("DataChange_LastTime", pojo.getDatachangeLasttime());
	
			return map;
		}
	}
}