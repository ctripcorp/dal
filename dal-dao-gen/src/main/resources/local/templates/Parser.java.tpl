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
		"${field.getName()}"#if($foreach.count != $fields.size()), #end
		#end
	};
	
	@Override
	public $table_name map(ResultSet rs, int rowNum) throws SQLException {
		$table_name vo = new $table_name();
		#foreach( $field in $fields )
vo.set${field.getName()}(rs.get$WordUtils.capitalize($field.getType())("${field.getName()}"));
		#end
return vo;
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
	public Map<String, ?> getFields(Person pojo) {
		Map<String, Object> map = new HashMap<String, Object>();
		#foreach( $field in $fields )
map.put("${field.getName()}", pojo.get${field.getName()}());
		#end
return map;
	}

	@Override
	public boolean hasIdentityColumn() {
		return $hasIdentity;
	}

	@Override
	public String getIdentityColumnName() {
		return #if($hasIdentity)"$identityColumn"#{else}null#end;
	}

	@Override
	public Number getIdentityValue(Person pojo) {
		return #if($hasIdentity)pojo.get${identityColumn}()#{else}null#end;
	}

	@Override
	public Map<String, ?> getPk(Person pojo) {
		Map<String, Object> map = new HashMap<String, Object>();
		#foreach( $field in $fields )
#if($field.isPrimary())map.put("${field.getName()}", pojo.get${field.getName()}());#end
		#end
return map;
	}
}
