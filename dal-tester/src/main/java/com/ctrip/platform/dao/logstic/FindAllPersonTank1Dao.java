package com.ctrip.platform.dao.logstic;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class FindAllPersonTank1Dao {

	private static final String DATA_BASE = "dao_test";
	
	private static final String ALL_SQL_PATTERN = "SELECT * FROM FindAllPerson";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from FindAllPerson";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM FindAllPerson LIMIT %s, %s";
			
	private DalClient client;
	private FindAllPersonTank1RowMapper mapper;
	private DalRowMapperExtractor<FindAllPersonTank1> extractor;
	private DalScalarExtractor scalarExtractor;
	
	/**
	 * Initialize the instance of Hotel2GenDao
	 */
	public FindAllPersonTank1Dao()
	{
		this.client = DalClientFactory.getClient(DATA_BASE);
		this.mapper = new FindAllPersonTank1RowMapper();
		this.extractor = new DalRowMapperExtractor<FindAllPersonTank1>(this.mapper);
		this.scalarExtractor = new DalScalarExtractor();
	}

	/**
	  *Get all FindAllPersonTank1 instances
	  *@return 
	  *     FindAllPersonTank1 collection
	**/
	public List<FindAllPersonTank1> getAll(DalHints hints) throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<FindAllPersonTank1> result = null;
		result = this.client.query(ALL_SQL_PATTERN, parameters, hints, extractor);
		return result;
	}
	
	/**
	  *Get the count of FindAllPersonTank1 instances
	  *@return 
	  *     the FindAllPersonTank1 records count
	**/
	public int Count(DalHints hints) throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);	
		Number result = (Number)this.client.query(COUNT_SQL_PATTERN, parameters, hints, scalarExtractor);
		return result.intValue();
	}
	
	public List<FindAllPersonTank1> getListByPage(int pagesize, int pageNo,DalHints hints) throws SQLException
	{
		if(pageNo < 1 || pagesize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");
		
        StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);	
		
		String sql = "";
		sql = String.format(PAGE_MYSQL_PATTERN, (pageNo - 1) * pagesize, pagesize);
		return this.client.query(sql, parameters, hints, extractor);
	}

	/**
	  * Map the sql result-set to FindAllPersonTank1 instance
	**/
	private class FindAllPersonTank1RowMapper implements DalRowMapper<FindAllPersonTank1> {

		@Override
		public FindAllPersonTank1 map(ResultSet rs, int rowNum) throws SQLException {
			FindAllPersonTank1 pojo = new FindAllPersonTank1();
			
			pojo.setID((Integer)rs.getObject("ID"));
			pojo.setAddress((String)rs.getObject("Address"));
			pojo.setTelephone((String)rs.getObject("Telephone"));
			pojo.setName((String)rs.getObject("Name"));
			pojo.setAge((Integer)rs.getObject("Age"));
			pojo.setGender((Integer)rs.getObject("Gender"));
			pojo.setBirth((Timestamp)rs.getObject("Birth"));

			return pojo;
		}
	}
}