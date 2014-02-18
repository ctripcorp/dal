package ${pojoHost.getDaoNamespace()};

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalParser;

public class ${parserHost.getClassName()} implements DalParser<${pojoHost.getClassName()}> {
	public static final String DATABASE_NAME = "${parserHost.getDbName()}";
	public static final String TABLE_NAME = "${parserHost.getTableName()}";
	private static final String[] COLUMNS = new String[]{
#foreach( $field in ${pojoHost.getFields()} )
$tab$tab"${field.getName()}"#if($foreach.count != $fields.size()),${newline}#end
#end
	};
	
	@Override
	public ${pojoHost.getClassName()} map(ResultSet rs, int rowNum) throws SQLException {
		${pojoHost.getClassName()} pojo = new ${pojoHost.getClassName()};
#foreach( $field in ${pojoHost.getFields()} )
${tab}${tab}pojo.set${field.getName()}(rs.get$WordUtils.capitalize($field.getType())("${field.getName()}"));
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
	public Map<String, ?> getFields(${pojoHost.getClassName()} pojo) {
		Map<String, Object> map = new HashMap<String, Object>();
#foreach( $field in ${pojoHost.getFields()} )
		map.put("${field.getName()}", pojo.get${field.getName()}());
#end
		return map;
	}

	@Override
	public boolean hasIdentityColumn() {
		return ${parserHost.isHasIdentity()};
	}

	@Override
	public String getIdentityColumnName() {
		return #if($parserHost.isHasIdentity())"${parserHost.getIdentityColumnName()}"#{else}null#end;
	}

	@Override
	public Number getIdentityValue(${pojoHost.getClassName()} pojo) {
		return #if($parserHost.isHasIdentity())pojo.get${parserHost.getIdentityColumnName()}()#{else}null#end;
	}

	@Override
	public Map<String, ?> getPk(${pojoHost.getClassName()} pojo) {
		Map<String, Object> map = new HashMap<String, Object>();
#foreach( $field in ${pojoHost.getFields()} )
#if($field.isPrimary())${tab}${tab}map.put("${field.getName()}",pojo.get${field.getName()}());#end
#end
${newline}${tab}${tab}return map;
	}
}
