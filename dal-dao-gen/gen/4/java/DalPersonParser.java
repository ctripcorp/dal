package hjhTest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalParser;

public class DalPersonParser implements DalParser<Person> {
	public static final String DATABASE_NAME = "dao_test";
	public static final String TABLE_NAME = "Person";
	private static final String[] COLUMNS = new String[]{
			"ID",	"Address",	"Telephone",	"Name",	"Age",	"Gender",	"Birth",};
	
	private static final int[] COLUMN_TYPES = new int[]{
			"4",
			"12",
			"12",
			"12",
			"4",
			"4",
			"93",
		};
	
	@Override
	public Person map(ResultSet rs, int rowNum) throws SQLException {
		Person pojo = new Person();
		
		pojo.setID(rs.getInt("ID"));
		pojo.setAddress(rs.getString("Address"));
		pojo.setTelephone(rs.getString("Telephone"));
		pojo.setName(rs.getString("Name"));
		pojo.setAge(rs.getInt("Age"));
		pojo.setGender(rs.getInt("Gender"));
		pojo.setBirth(rs.getTimestamp("Birth"));
				
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
		Map<String, Object> primaryKeys = new HashMap<String, Object>();
		
		primaryKeys.put("ID", pojo.getID());														
		return primaryKeys;
	}
	
	@Override
	public Map<String, ?> getFields(Person pojo) {
		Map<String, Object> fields = new HashMap<String, Object>();
		
		fields.put("ID", pojo.getID());
		fields.put("Address", pojo.getAddress());
		fields.put("Telephone", pojo.getTelephone());
		fields.put("Name", pojo.getName());
		fields.put("Age", pojo.getAge());
		fields.put("Gender", pojo.getGender());
		fields.put("Birth", pojo.getBirth());
		
		return fields;
	}
}
