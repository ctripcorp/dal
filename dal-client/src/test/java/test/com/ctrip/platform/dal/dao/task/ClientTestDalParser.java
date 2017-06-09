package test.com.ctrip.platform.dal.dao.task;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.helper.AbstractDalParser;

public class ClientTestDalParser extends AbstractDalParser<ClientTestModel>{
	private static final String tableName= "dal_client_test";
	private static final String[] columnNames = new String[]{
		"id","quantity","dbIndex","tableIndex","type","address","last_changed"
	};
	private static final String[] primaryKeyNames = new String[]{"id"};
	private static final int[] columnTypes = new int[]{
		Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR, Types.TIMESTAMP
	};
	
	public ClientTestDalParser(String databaseName) {
		super(databaseName, tableName, columnNames, primaryKeyNames, columnTypes);
	}
	
	private boolean _DEBUG = false;
	
	@Override
	public ClientTestModel map(ResultSet rs, int rowNum)
			throws SQLException {
        if(_DEBUG) {
            ResultSetMetaData rsmd = rs.getMetaData();
            String[] columns = new String[rsmd.getColumnCount()];
            for(int i = 0; i < columns.length; i++) {
                columns[i] = rsmd.getColumnName(i + 1);
            }
        }
	    
		ClientTestModel model = new ClientTestModel();
		model.setId(rs.getInt(1));
		model.setQuantity(getInteger(rs.getObject(2)));
		model.setDbIndex(getInteger(rs.getObject(3)));
		model.setTableIndex(getInteger(rs.getObject(4)));
		model.setType(rs.getShort(5));
		model.setAddress(rs.getString(6));
		model.setLastChanged(rs.getTimestamp(7));
		return model;
	}
	
	private Integer getInteger(Object value) {
		if(value == null)
			return null;
		
		return ((Number)value).intValue();
	}

	@Override
	public boolean isAutoIncrement() {
		return true;
	}

	@Override
	public Number getIdentityValue(ClientTestModel pojo) {
		return pojo.getId();
	}

	@Override
	public Map<String, ?> getPrimaryKeys(ClientTestModel pojo) {
		Map<String, Object> keys = new LinkedHashMap<String, Object>();
		keys.put("id", pojo.getId());
		return keys;
	}

	@Override
	public Map<String, ?> getFields(ClientTestModel pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("id", pojo.getId());
		map.put("quantity", pojo.getQuantity());
		map.put("dbIndex", pojo.getDbIndex());
		map.put("tableIndex", pojo.getTableIndex());
		map.put("type", pojo.getType());
		map.put("address", pojo.getAddress());
		map.put("last_changed", pojo.getLastChanged());
		return map;
	}
}
