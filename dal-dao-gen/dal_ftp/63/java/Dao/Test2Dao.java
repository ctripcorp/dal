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

public class Test2Dao {

	private static final String DATA_BASE = "AccCorpDB_INSERT_1";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM test2";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from test2";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM test2 WHERE LIMIT %s, %s";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by AccBalanceID desc ) as rownum" 
			+" from test2 (nolock)) select * from CTE where rownum between %s and %s";
			
	private DalClient client;
	private Test2RowMapper mapper;
	private DalRowMapperExtractor<Test2> extractor;
	private DalScalarExtractor scalarExtractor;
	
	/**
	 * Initialize the instance of Hotel2GenDao
	 */
	public Test2Dao()
	{
		this.client = DalClientFactory.getClient(DATA_BASE);
		this.mapper = new Test2RowMapper();
		this.extractor = new DalRowMapperExtractor<Test2>(this.mapper);
		this.scalarExtractor = new DalScalarExtractor();
	}

	/**
	  *Get all Test2 instances
	  *@return 
	  *     Test2 collection
	**/
	public List<Test2> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<Test2> result = null;
		result = this.client.query(ALL_SQL_PATTERN, parameters, hints, extractor);
		return result;
	}
	
	/**
	  *Get the count of Test2 instances
	  *@return 
	  *     the Test2 records count
	**/
	public long Count() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		long result = (Long)this.client.query(COUNT_SQL_PATTERN, parameters, hints, scalarExtractor);
		return result;
	}
	
	public List<Test2> getListByPage(Test2 obj, int pagesize, int pageNo) throws SQLException
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
	  * Map the sql result-set to Test2 instance
	**/
	private class Test2RowMapper implements DalRowMapper<Test2> {

		@Override
		public Test2 map(ResultSet rs, int rowNum) throws SQLException {
			Test2 pojo = new Test2();
			
			pojo.setAccBalanceID((Integer)rs.getObject("AccBalanceID"));
			pojo.setBatchNo((String)rs.getObject("BatchNo"));
			pojo.setAccountID((Integer)rs.getObject("AccountID"));
			pojo.setCreateTime((Timestamp)rs.getObject("CreateTime"));
			pojo.setStartDate((String)rs.getObject("StartDate"));
			pojo.setEndDate((String)rs.getObject("EndDate"));
			pojo.setRecReturn((BigDecimal)rs.getObject("recReturn"));
			pojo.setReportCompletionDay((Timestamp)rs.getObject("ReportCompletionDay"));
			pojo.setContractFirmDay((Timestamp)rs.getObject("ContractFirmDay"));
			pojo.setContractDay((Timestamp)rs.getObject("ContractDay"));
			pojo.setReConfirmDate((Timestamp)rs.getObject("ReConfirmDate"));
			pojo.setContractDate((Timestamp)rs.getObject("ContractDate"));
			pojo.setLastAuditDate((Timestamp)rs.getObject("LastAuditDate"));
			pojo.setSendRptDate((Timestamp)rs.getObject("SendRptDate"));
			pojo.setAuditDate((Timestamp)rs.getObject("AuditDate"));
			pojo.setReceiveDate((Timestamp)rs.getObject("ReceiveDate"));
			pojo.setRptDate((Timestamp)rs.getObject("RptDate"));
			pojo.setConfirmDate((Timestamp)rs.getObject("ConfirmDate"));
			pojo.setBalDate((Timestamp)rs.getObject("BalDate"));
			pojo.setInvoiceDate((Timestamp)rs.getObject("InvoiceDate"));
			pojo.setConfirmFlag((String)rs.getObject("ConfirmFlag"));
			pojo.setLastAuditFlag((String)rs.getObject("LastAuditFlag"));
			pojo.setSettleFlag((String)rs.getObject("SettleFlag"));

			return pojo;
		}
	}
}
