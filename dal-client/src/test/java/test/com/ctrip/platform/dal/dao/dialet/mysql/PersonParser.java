package test.com.ctrip.platform.dal.dao.dialet.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.helper.AbstractDalParser;

public class PersonParser extends AbstractDalParser<Person> {
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
		"PartmentID",
	};
	
	private static final String[] PRIMARY_KEYS = new String[]{
		"ID",
	};
	
	private static final int[] COLUMN_TYPES = new int[]{
		Types.INTEGER,
		Types.VARCHAR,
		Types.VARCHAR,
		Types.VARCHAR,
		Types.INTEGER,
		Types.INTEGER,
		Types.TIMESTAMP,
		Types.INTEGER,
	};
	
	public PersonParser() {
		super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
	}
	
	@Override
	public Person map(ResultSet rs, int rowNum) throws SQLException {
		Person pojo = new Person();
		
		pojo.setID(rs.getInt("ID"));
		pojo.setAddress((String)rs.getObject("Address"));
		pojo.setTelephone((String)rs.getObject("Telephone"));
		pojo.setName((String)rs.getObject("Name"));
		pojo.setAge(rs.getInt("Age"));
		pojo.setGender(rs.getInt("Gender"));
		pojo.setBirth((Timestamp)rs.getObject("Birth"));
		pojo.setPartmentID(rs.getInt("PartmentID"));

		return pojo;
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
		map.put("PartmentID", pojo.getPartmentID());

		return map;
	}
}
