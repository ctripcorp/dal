package com.ctrip.dal.test.test4;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

public class TestDao {

	private static final String DATA_BASE = "AccCorpDB_INSERT_1";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM test";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from test";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM test WHERE LIMIT %s, %s";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by id desc ) as rownum" 
			+" from test (nolock)) select * from CTE where rownum between %s and %s";
			
	private DalClient client;
	private TestRowMapper mapper;
	private DalRowMapperExtractor<Test> extractor;
	private DalScalarExtractor scalarExtractor;
	
	/**
	 * Initialize the instance of Hotel2GenDao
	 */
	public TestDao()
	{
		this.client = DalClientFactory.getClient(DATA_BASE);
		this.mapper = new TestRowMapper();
		this.extractor = new DalRowMapperExtractor<Test>(this.mapper);
		this.scalarExtractor = new DalScalarExtractor();
	}

	/**
	  *Get all Test instances
	  *@return 
	  *     Test collection
	**/
	public List<Test> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<Test> result = null;
		result = this.client.query(ALL_SQL_PATTERN, parameters, hints, extractor);
		return result;
	}
	
	/**
	  *Get the count of Test instances
	  *@return 
	  *     the Test records count
	**/
	public long Count() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		long result = (Long)this.client.query(COUNT_SQL_PATTERN, parameters, hints, scalarExtractor);
		return result;
	}
	
	public List<Test> getListByPage(Test obj, int pagesize, int pageNo) throws SQLException
	{
		if(pageNo < 1 || pagesize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");
		
        StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String sql = "";
		int fromRownum = (pageNo - 1) * pagesize + 1;
        int endRownum = pagesize * pageNo;
		sql = String.format(PAGE_SQL_PATTERN, fromRownum, endRownum);
		return this.client.query(sql, parameters, hints, extractor);
	}

	/**
	  * Map the sql result-set to Test instance
	**/
	private class TestRowMapper implements DalRowMapper<Test> {

		@Override
		public Test map(ResultSet rs, int rowNum) throws SQLException {
			Test pojo = new Test();
			
			pojo.setId((Integer)rs.getObject("id"));
			pojo.setStatus((String)rs.getObject("status"));
			pojo.setNumber((Integer)rs.getObject("number"));
			pojo.setAccBalanceID((Integer)rs.getObject("AccBalanceID"));
			pojo.setAccountID((Integer)rs.getObject("AccountID"));
			pojo.setStartDate((String)rs.getObject("StartDate"));
			pojo.setAccountName((String)rs.getObject("AccountName"));
			pojo.setCompanyName((String)rs.getObject("CompanyName"));
			pojo.setAccountType((String)rs.getObject("accountType"));
			pojo.setAccountTypeName((String)rs.getObject("accountTypeName"));
			pojo.setCheckAccType((Integer)rs.getObject("CheckAccType"));
			pojo.setCheckAccTypeName((String)rs.getObject("CheckAccTypeName"));
			pojo.setFltconMoney((BigDecimal)rs.getObject("fltconMoney"));
			pojo.setHtlXconMoney((BigDecimal)rs.getObject("HtlXconMoney"));
			pojo.setHtlHconMoney((BigDecimal)rs.getObject("HtlHconMoney"));
			pojo.setSettleConsume((String)rs.getObject("settleConsume"));
			pojo.setSettleService((String)rs.getObject("settleService"));
			pojo.setSettleReturn((String)rs.getObject("settleReturn"));
			pojo.setConappointed((String)rs.getObject("conappointed"));
			pojo.setContractFirmDay((Timestamp)rs.getObject("ContractFirmDay"));
			pojo.setContractDay((Timestamp)rs.getObject("ContractDay"));
			pojo.setInvoiceDate((Timestamp)rs.getObject("InvoiceDate"));
			pojo.setReportCompletionDay((Timestamp)rs.getObject("ReportCompletionDay"));
			pojo.setRptDate((Timestamp)rs.getObject("RptDate"));
			pojo.setAuditDate((Timestamp)rs.getObject("AuditDate"));
			pojo.setSendRptDate((Timestamp)rs.getObject("SendRptDate"));
			pojo.setConfirmDate((Timestamp)rs.getObject("ConfirmDate"));
			pojo.setLastAuditDate((Timestamp)rs.getObject("LastAuditDate"));
			pojo.setRptDateOper((String)rs.getObject("RptDateOper"));
			pojo.setConfirmDateOper((String)rs.getObject("ConfirmDateOper"));
			pojo.setIsDailyAudit((String)rs.getObject("IsDailyAudit"));
			pojo.setReConfirmflag((String)rs.getObject("ReConfirmflag"));

			return pojo;
		}
	}
}
