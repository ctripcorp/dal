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

public class SenderGenDao {
	private static final String DATA_BASE = "AccProductDB_INSERT_2";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from Sender";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM Sender";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by Sender desc ) as rownum" 
			+" from Sender (nolock)) select * from CTE where rownum between %s and %s";
	
	private DalParser<SenderGen> parser = new SenderGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<SenderGen> rowextractor = null;
	private DalTableDao<SenderGen> client;
	private DalClient baseClient;

	public SenderGenDao() {
		this.client = new DalTableDao<SenderGen>(parser);
		this.rowextractor = new DalRowMapperExtractor<SenderGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public SenderGen queryByPk(String sender)
			throws SQLException {
		DalHints hints = new DalHints();
		SenderGen pk = new SenderGen();
			
		pojo.setSender(sender);

		return client.queryByPk(pk, hints);
	}

	public SenderGen queryByPk(SenderGen pk)
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
	
	public List<SenderGen> queryByPage(SenderGen pk, int pageSize, int pageNo)  throws SQLException {
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
	
	public List<SenderGen> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<SenderGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

	public void insert(SenderGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.insert(hints, null, daoPojos);
	}

	public void insert(KeyHolder keyHolder, SenderGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.insert(hints, keyHolder, daoPojos);
	}

	public void delete(SenderGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.delete(hints, daoPojos);
	}

	public void update(SenderGen...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.update(hints, daoPojos);
	}

    public List<SenderGen> iii(String AtWork) 
			throws SQLException {
		String sql = "SELECT * FROM Sender WHERE  BETWEEN ? AND ? ";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
		parameters.set(i++, Types.CHAR, AtWork);
		return queryDao.query(sql, parameters, hints, personRowMapper);
	}

	private static class SenderGenParser extends AbstractDalParser<SenderGen> {
		public static final String DATABASE_NAME = "AccProductDB_INSERT_2";
		public static final String TABLE_NAME = "Sender";
		private static final String[] COLUMNS = new String[]{
			"Sender",
			"SenderName",
			"SenderCity",
			"SendSite",
			"ContactTel",
			"MobilePhone",
			"BirthDay",
			"Gender",
			"ContactAddr",
			"IDCardNo",
			"IDCardAddr",
			"Warrantor",
			"WarrantorAddr",
			"WarrantorIDCard",
			"WarrantorTel",
			"AtWork",
			"Dept",
			"IsDelete",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"Sender",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.VARCHAR,
			Types.VARCHAR,
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.TIMESTAMP,
			Types.CHAR,
			Types.VARCHAR,
			Types.CHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.CHAR,
			Types.VARCHAR,
			Types.CHAR,
			Types.CHAR,
			Types.INTEGER,
		};
		
		public SenderGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public SenderGen map(ResultSet rs, int rowNum) throws SQLException {
			SenderGen pojo = new SenderGen();
			
			pojo.setSender((String)rs.getObject("Sender"));
			pojo.setSenderName((String)rs.getObject("SenderName"));
			pojo.setSenderCity((Integer)rs.getObject("SenderCity"));
			pojo.setSendSite((Integer)rs.getObject("SendSite"));
			pojo.setContactTel((String)rs.getObject("ContactTel"));
			pojo.setMobilePhone((String)rs.getObject("MobilePhone"));
			pojo.setBirthDay((Timestamp)rs.getObject("BirthDay"));
			pojo.setGender((String)rs.getObject("Gender"));
			pojo.setContactAddr((String)rs.getObject("ContactAddr"));
			pojo.setIDCardNo((String)rs.getObject("IDCardNo"));
			pojo.setIDCardAddr((String)rs.getObject("IDCardAddr"));
			pojo.setWarrantor((String)rs.getObject("Warrantor"));
			pojo.setWarrantorAddr((String)rs.getObject("WarrantorAddr"));
			pojo.setWarrantorIDCard((String)rs.getObject("WarrantorIDCard"));
			pojo.setWarrantorTel((String)rs.getObject("WarrantorTel"));
			pojo.setAtWork((String)rs.getObject("AtWork"));
			pojo.setDept((String)rs.getObject("Dept"));
			pojo.setIsDelete((Integer)rs.getObject("IsDelete"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return false;
		}
	
		@Override
		public Number getIdentityValue(SenderGen pojo) {
			return null;
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(SenderGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("Sender", pojo.getSender());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(SenderGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("Sender", pojo.getSender());
			map.put("SenderName", pojo.getSenderName());
			map.put("SenderCity", pojo.getSenderCity());
			map.put("SendSite", pojo.getSendSite());
			map.put("ContactTel", pojo.getContactTel());
			map.put("MobilePhone", pojo.getMobilePhone());
			map.put("BirthDay", pojo.getBirthDay());
			map.put("Gender", pojo.getGender());
			map.put("ContactAddr", pojo.getContactAddr());
			map.put("IDCardNo", pojo.getIDCardNo());
			map.put("IDCardAddr", pojo.getIDCardAddr());
			map.put("Warrantor", pojo.getWarrantor());
			map.put("WarrantorAddr", pojo.getWarrantorAddr());
			map.put("WarrantorIDCard", pojo.getWarrantorIDCard());
			map.put("WarrantorTel", pojo.getWarrantorTel());
			map.put("AtWork", pojo.getAtWork());
			map.put("Dept", pojo.getDept());
			map.put("IsDelete", pojo.getIsDelete());
	
			return map;
		}
	}
}