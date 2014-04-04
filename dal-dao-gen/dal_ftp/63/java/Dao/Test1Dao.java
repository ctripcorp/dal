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

public class Test1Dao {

	private static final String DATA_BASE = "AccCorpDB_INSERT_1";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM test1";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from test1";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM test1 WHERE LIMIT %s, %s";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by HtlOrderDetailId desc ) as rownum" 
			+" from test1 (nolock)) select * from CTE where rownum between %s and %s";
			
	private DalClient client;
	private Test1RowMapper mapper;
	private DalRowMapperExtractor<Test1> extractor;
	private DalScalarExtractor scalarExtractor;
	
	/**
	 * Initialize the instance of Hotel2GenDao
	 */
	public Test1Dao()
	{
		this.client = DalClientFactory.getClient(DATA_BASE);
		this.mapper = new Test1RowMapper();
		this.extractor = new DalRowMapperExtractor<Test1>(this.mapper);
		this.scalarExtractor = new DalScalarExtractor();
	}

	/**
	  *Get all Test1 instances
	  *@return 
	  *     Test1 collection
	**/
	public List<Test1> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<Test1> result = null;
		result = this.client.query(ALL_SQL_PATTERN, parameters, hints, extractor);
		return result;
	}
	
	/**
	  *Get the count of Test1 instances
	  *@return 
	  *     the Test1 records count
	**/
	public long Count() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		long result = (Long)this.client.query(COUNT_SQL_PATTERN, parameters, hints, scalarExtractor);
		return result;
	}
	
	public List<Test1> getListByPage(Test1 obj, int pagesize, int pageNo) throws SQLException
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
	  * Map the sql result-set to Test1 instance
	**/
	private class Test1RowMapper implements DalRowMapper<Test1> {

		@Override
		public Test1 map(ResultSet rs, int rowNum) throws SQLException {
			Test1 pojo = new Test1();
			
			pojo.setHtlOrderDetailId((Integer)rs.getObject("HtlOrderDetailId"));
			pojo.setOrderId((Integer)rs.getObject("OrderId"));
			pojo.setOrderType((String)rs.getObject("OrderType"));
			pojo.setAmount((BigDecimal)rs.getObject("Amount"));
			pojo.setServiceFee((BigDecimal)rs.getObject("ServiceFee"));
			pojo.setRebate((BigDecimal)rs.getObject("Rebate"));
			pojo.setIsInbatch((String)rs.getObject("IsInbatch"));
			pojo.setAccCheckId((Integer)rs.getObject("AccCheckId"));
			pojo.setCreatTime((Timestamp)rs.getObject("CreatTime"));
			pojo.setInbatchTime((Timestamp)rs.getObject("InbatchTime"));
			pojo.setLastModifyTime((Timestamp)rs.getObject("LastModifyTime"));
			pojo.setAccountId((Integer)rs.getObject("AccountId"));
			pojo.setSubAccountID((Integer)rs.getObject("SubAccountID"));
			pojo.setRid((Integer)rs.getObject("Rid"));
			pojo.setRCTime((Timestamp)rs.getObject("RCTime"));
			pojo.setRCQuantity((Integer)rs.getObject("RCQuantity"));

			return pojo;
		}
	}
}
