
package hjhTest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalParser;

public class DalPersonParser implements DalParser<Person> {
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
	public Person map(ResultSet rs, int rowNum) throws SQLException {
		Person pojo = new Person();
		
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
	public Number getIdentityValue(Person pojo) {
		return pojo.getID();
	}

	@Override
	public Map<String, ?> getPrimaryKeys(Person pojo) {
		Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
		
		primaryKeys.put("ID", pojo.getID());

		return primaryKeys;
	}

	@Override
	public Map<String, ?> getFields(Person pojo) {
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
