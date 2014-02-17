package $namespace;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalParser;

public class Dal${table_name}Parser implements DalParser<$table_name> {
	public static final String DATABASE_NAME = "$database";
	public static final String TABLE_NAME = "$table_name";
	private static final String[] COLUMNS = new String[]{
#foreach( $field in $fields )
		"${field.getName()}",
#end
	};
	
	private static final String[] PRIMARY_KEYS = new String[]{
#foreach( $field in $fields )
#if($field.isPrimary())
		"${field.getName()}",
#end
#end
	};
	
	private static final int[] COLUMN_TYPES = new int[]{
#foreach( $field in $fields )
		${field.getDataType()},
#end
	};
	
	@Override
	public $table_name map(ResultSet rs, int rowNum) throws SQLException {
		$table_name pojo = new $table_name();
		
#foreach( $field in $fields )
		pojo.set${field.getName()}(rs.get$WordUtils.capitalize($field.getType())("${field.getName()}"));
#end
		
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
		return $hasIdentity;
	}

	@Override
	public Number getIdentityValue(Person pojo) {
		return #if($hasIdentity)pojo.get${identityColumn}()#{else}null#end;
	}

	@Override
	public Map<String, ?> getPrimaryKeys(Person pojo) {
		Map<String, Object> primaryKeys = new HashMap<String, Object>();
		
#foreach( $field in $fields )
#if($field.isPrimary())
		primaryKeys.put("${field.getName()}", pojo.get${field.getName()}());
#end
#end

		return primaryKeys;
	}
	
	@Override
	public Map<String, ?> getFields(Person pojo) {
		Map<String, Object> fields = new HashMap<String, Object>();
		
#foreach( $field in $fields )
		fields.put("${field.getName()}", pojo.get${field.getName()}());
#end

		return fields;
	}
}
