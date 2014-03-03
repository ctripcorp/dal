package ${host.getNamespace()};

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

public class MoneyTest_genDao {
	private DalTableDao<MoneyTest_gen> client = new DalTableDao<MoneyTest_gen>(new MoneyTest_genParser());
	private DalClient baseClient = DalClientFactory.getClient(parser.getDatabaseName());

	public MoneyTest_gen queryByPk(Number id, DalHints hints)
			throws SQLException {
		return client.queryByPk(id, hints);
	}

	public MoneyTest_gen queryByPk(MoneyTest_gen pk, DalHints hints)
			throws SQLException {
		return client.queryByPk(pk, hints);
	}
	
	public List<MoneyTest_gen> queryByPage(MoneyTest_gen pk, int pageSize, int pageNo, DalHints hints)
			throws SQLException {
		// TODO to be implemented
		return null;
	}
	
	public void insert(DalHints hints, MoneyTest_gen...daoPojos) throws SQLException {
		client.insert(hints, null, daoPojos);
	}

	public void insert(DalHints hints, KeyHolder keyHolder, MoneyTest_gen...daoPojos) throws SQLException {
		client.insert(hints, keyHolder, daoPojos);
	}
	
	public void delete(DalHints hints, MoneyTest_gen...daoPojos) throws SQLException {
		client.delete(hints, daoPojos);
	}
	
	public void update(DalHints hints, MoneyTest_gen...daoPojos) throws SQLException {
		client.update(hints, daoPojos);
	}


	private static class MoneyTest_genParser implements DalParser<MoneyTest_gen> {
		public static final String DATABASE_NAME = "dao_test";
		public static final String TABLE_NAME = "MoneyTest";
		private static final String[] COLUMNS = new String[]{
			"id",
			"money_all",
			"bool_test",
			"date_test",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"id",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			4,
			3,
			-7,
			93,
		};
		
		@Override
		public MoneyTest_gen map(ResultSet rs, int rowNum) throws SQLException {
			MoneyTest_gen pojo = new MoneyTest_gen();
			
			pojo.setid((Integer)rs.getObject("id"));
			pojo.setmoney_all((BigDecimal)rs.getObject("money_all"));
			pojo.setbool_test((Boolean)rs.getObject("bool_test"));
			pojo.setdate_test((Timestamp)rs.getObject("date_test"));
	
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
		public Number getIdentityValue(MoneyTest_gen pojo) {
			return pojo.getid();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(MoneyTest_gen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("id", pojo.getid());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(MoneyTest_gen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("id", pojo.getid());
			map.put("money_all", pojo.getmoney_all());
			map.put("bool_test", pojo.getbool_test());
			map.put("date_test", pojo.getdate_test());
	
			return map;
		}
	}
}
