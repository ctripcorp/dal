package ${host.getNamespace()};

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

public class Person_genDao {
	private DalTableDao<Person_gen> client = new DalTableDao<Person_gen>(new Person_genParser());
	private DalClient baseClient = DalClientFactory.getClient(parser.getDatabaseName());

	public Person_gen queryByPk(Number id, DalHints hints)
			throws SQLException {
		return client.queryByPk(id, hints);
	}

	public Person_gen queryByPk(Person_gen pk, DalHints hints)
			throws SQLException {
		return client.queryByPk(pk, hints);
	}
	
	public List<Person_gen> queryByPage(Person_gen pk, int pageSize, int pageNo, DalHints hints)
			throws SQLException {
		// TODO to be implemented
		return null;
	}
	
	public void insert(DalHints hints, Person_gen...daoPojos) throws SQLException {
		client.insert(hints, null, daoPojos);
	}

	public void insert(DalHints hints, KeyHolder keyHolder, Person_gen...daoPojos) throws SQLException {
		client.insert(hints, keyHolder, daoPojos);
	}
	
	public void delete(DalHints hints, Person_gen...daoPojos) throws SQLException {
		client.delete(hints, daoPojos);
	}
	
	public void update(DalHints hints, Person_gen...daoPojos) throws SQLException {
		client.update(hints, daoPojos);
	}


	private static class Person_genParser implements DalParser<Person_gen> {
		public static final String DATABASE_NAME = "dao_test";
		public static final String TABLE_NAME = "Person";
		private static final String[] COLUMNS = new String[]{
			"ID",
			"Address",
			"Telephone",
			"Name",
			"Age",
			"Gender",
			"Birth",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"ID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			4,
			12,
			12,
			12,
			4,
			4,
			93,
		};
		
		@Override
		public Person_gen map(ResultSet rs, int rowNum) throws SQLException {
			Person_gen pojo = new Person_gen();
			
			pojo.setID((Integer)rs.getObject("ID"));
			pojo.setAddress((String)rs.getObject("Address"));
			pojo.setTelephone((String)rs.getObject("Telephone"));
			pojo.setName((String)rs.getObject("Name"));
			pojo.setAge((Integer)rs.getObject("Age"));
			pojo.setGender((Integer)rs.getObject("Gender"));
			pojo.setBirth((Timestamp)rs.getObject("Birth"));
	
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
		public Number getIdentityValue(Person_gen pojo) {
			return pojo.getID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(Person_gen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("ID", pojo.getID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(Person_gen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("ID", pojo.getID());
			map.put("Address", pojo.getAddress());
			map.put("Telephone", pojo.getTelephone());
			map.put("Name", pojo.getName());
			map.put("Age", pojo.getAge());
			map.put("Gender", pojo.getGender());
			map.put("Birth", pojo.getBirth());
	
			return map;
		}
	}
}
