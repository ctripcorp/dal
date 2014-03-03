package ${host.getNamespace()};

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

public class ManyTypes_genDao {
	private DalTableDao<ManyTypes_gen> client = new DalTableDao<ManyTypes_gen>(new ManyTypes_genParser());
	private DalClient baseClient = DalClientFactory.getClient(parser.getDatabaseName());

	public ManyTypes_gen queryByPk(Number id, DalHints hints)
			throws SQLException {
		return client.queryByPk(id, hints);
	}

	public ManyTypes_gen queryByPk(ManyTypes_gen pk, DalHints hints)
			throws SQLException {
		return client.queryByPk(pk, hints);
	}
	
	public List<ManyTypes_gen> queryByPage(ManyTypes_gen pk, int pageSize, int pageNo, DalHints hints)
			throws SQLException {
		// TODO to be implemented
		return null;
	}
	
	public void insert(DalHints hints, ManyTypes_gen...daoPojos) throws SQLException {
		client.insert(hints, null, daoPojos);
	}

	public void insert(DalHints hints, KeyHolder keyHolder, ManyTypes_gen...daoPojos) throws SQLException {
		client.insert(hints, keyHolder, daoPojos);
	}
	
	public void delete(DalHints hints, ManyTypes_gen...daoPojos) throws SQLException {
		client.delete(hints, daoPojos);
	}
	
	public void update(DalHints hints, ManyTypes_gen...daoPojos) throws SQLException {
		client.update(hints, daoPojos);
	}


	private static class ManyTypes_genParser implements DalParser<ManyTypes_gen> {
		public static final String DATABASE_NAME = "dao_test";
		public static final String TABLE_NAME = "ManyTypes";
		private static final String[] COLUMNS = new String[]{
			"Id",
			"TinyIntCol",
			"SmallIntCol",
			"IntCol",
			"BigIntCol",
			"DecimalCol",
			"DoubleCol",
			"FloatCol",
			"BitCol",
			"CharCol",
			"VarCharCol",
			"DateCol",
			"DateTimeCol",
			"TimeCol",
			"TimestampCol",
			"YearCol",
			"BinaryCol",
			"BlobCol",
			"LongBlobCol",
			"MediumBlobCol",
			"TinyBlobCol",
			"VarBinaryCol",
			"LongTextCol",
			"MediumTextCol",
			"TextCol",
			"TinyTextCol",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"Id",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			4,
			-6,
			5,
			4,
			-5,
			3,
			8,
			7,
			-7,
			1,
			12,
			91,
			93,
			92,
			93,
			91,
			-2,
			-4,
			-4,
			-4,
			-2,
			-3,
			-1,
			-1,
			-1,
			12,
		};
		
		@Override
		public ManyTypes_gen map(ResultSet rs, int rowNum) throws SQLException {
			ManyTypes_gen pojo = new ManyTypes_gen();
			
			pojo.setId((Integer)rs.getObject("Id"));
			pojo.setTinyIntCol((Integer)rs.getObject("TinyIntCol"));
			pojo.setSmallIntCol((Integer)rs.getObject("SmallIntCol"));
			pojo.setIntCol((Integer)rs.getObject("IntCol"));
			pojo.setBigIntCol((Long)rs.getObject("BigIntCol"));
			pojo.setDecimalCol((BigDecimal)rs.getObject("DecimalCol"));
			pojo.setDoubleCol((Double)rs.getObject("DoubleCol"));
			pojo.setFloatCol((Float)rs.getObject("FloatCol"));
			pojo.setBitCol((Boolean)rs.getObject("BitCol"));
			pojo.setCharCol((String)rs.getObject("CharCol"));
			pojo.setVarCharCol((String)rs.getObject("VarCharCol"));
			pojo.setDateCol((Date)rs.getObject("DateCol"));
			pojo.setDateTimeCol((Timestamp)rs.getObject("DateTimeCol"));
			pojo.setTimeCol((Time)rs.getObject("TimeCol"));
			pojo.setTimestampCol((Timestamp)rs.getObject("TimestampCol"));
			pojo.setYearCol((Date)rs.getObject("YearCol"));
			pojo.setBinaryCol((byte[])rs.getObject("BinaryCol"));
			pojo.setBlobCol((byte[])rs.getObject("BlobCol"));
			pojo.setLongBlobCol((byte[])rs.getObject("LongBlobCol"));
			pojo.setMediumBlobCol((byte[])rs.getObject("MediumBlobCol"));
			pojo.setTinyBlobCol((byte[])rs.getObject("TinyBlobCol"));
			pojo.setVarBinaryCol((byte[])rs.getObject("VarBinaryCol"));
			pojo.setLongTextCol((String)rs.getObject("LongTextCol"));
			pojo.setMediumTextCol((String)rs.getObject("MediumTextCol"));
			pojo.setTextCol((String)rs.getObject("TextCol"));
			pojo.setTinyTextCol((String)rs.getObject("TinyTextCol"));
	
			return pojo;
		}
	
		@Override
		public String getDatabaseName() {
			return DATABASE_NAME;
		}
	
		@Override
		public String getTableName() {
			return TABLE_NAME;
		}
	
		@Override
		public String[] getColumnNames() {
			return COLUMNS;
		}
	
		@Override
		public String[] getPrimaryKeyNames() {
			return PRIMARY_KEYS;
		}
		
		@Override
		public int[] getColumnTypes() {
			return COLUMN_TYPES;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(ManyTypes_gen pojo) {
			return pojo.getId();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(ManyTypes_gen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("Id", pojo.getId());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(ManyTypes_gen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("Id", pojo.getId());
			map.put("TinyIntCol", pojo.getTinyIntCol());
			map.put("SmallIntCol", pojo.getSmallIntCol());
			map.put("IntCol", pojo.getIntCol());
			map.put("BigIntCol", pojo.getBigIntCol());
			map.put("DecimalCol", pojo.getDecimalCol());
			map.put("DoubleCol", pojo.getDoubleCol());
			map.put("FloatCol", pojo.getFloatCol());
			map.put("BitCol", pojo.getBitCol());
			map.put("CharCol", pojo.getCharCol());
			map.put("VarCharCol", pojo.getVarCharCol());
			map.put("DateCol", pojo.getDateCol());
			map.put("DateTimeCol", pojo.getDateTimeCol());
			map.put("TimeCol", pojo.getTimeCol());
			map.put("TimestampCol", pojo.getTimestampCol());
			map.put("YearCol", pojo.getYearCol());
			map.put("BinaryCol", pojo.getBinaryCol());
			map.put("BlobCol", pojo.getBlobCol());
			map.put("LongBlobCol", pojo.getLongBlobCol());
			map.put("MediumBlobCol", pojo.getMediumBlobCol());
			map.put("TinyBlobCol", pojo.getTinyBlobCol());
			map.put("VarBinaryCol", pojo.getVarBinaryCol());
			map.put("LongTextCol", pojo.getLongTextCol());
			map.put("MediumTextCol", pojo.getMediumTextCol());
			map.put("TextCol", pojo.getTextCol());
			map.put("TinyTextCol", pojo.getTinyTextCol());
	
			return map;
		}
	}
}
