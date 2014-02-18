
package ${pojoHost.getDaoNamespace()};

#foreach( $field in ${parserHost.getImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalParser;

public class ${parserHost.getClassName()} implements DalParser<${pojoHost.getClassName()}> {
	public static final String DATABASE_NAME = "${parserHost.getDbName()}";
	public static final String TABLE_NAME = "${parserHost.getTableName()}";
	private static final String[] COLUMNS = new String[]{
#foreach( $field in ${pojoHost.getFields()} )
		"${field.getName()}",
#end
	};
	
	private static final String[] PRIMARY_KEYS = new String[]{
#foreach( $field in ${pojoHost.getFields()} )
#if($field.isPrimary())
		"${field.getName()}",
#end
#end
	};
	
	private static final int[] COLUMN_TYPES = new int[]{
#foreach( $field in ${pojoHost.getFields()} )
		${field.getDataType()},
#end
	};
	
	@Override
	public ${pojoHost.getClassName()} map(ResultSet rs, int rowNum) throws SQLException {
		${pojoHost.getClassName()} pojo = new ${pojoHost.getClassName()};
		
#foreach( $field in ${pojoHost.getFields()} )
		pojo.set${field.getName()}((${field.getJavaClass().getSimpleName()})rs.getObject("${field.getName()}");
##${tab}${tab}pojo.set${field.getName()}(rs.get$WordUtils.capitalize($field.getType())("${field.getName()}"));
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
		return ${parserHost.isHasIdentity()};
	}

	@Override
	public Number getIdentityValue(Person pojo) {
		return #if($parserHost.isHasIdentity())pojo.get${parserHost.getIdentityColumnName()}()#{else}null#end;
	}

	@Override
	public Map<String, ?> getPrimaryKeys(Person pojo) {
		Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
		
#foreach( $field in ${pojoHost.getFields()} )
#if($field.isPrimary())
		primaryKeys.put("${field.getName()}", pojo.get${field.getName()}());
#end
#end

		return primaryKeys;
	}

	@Override
	public Map<String, ?> getFields(${pojoHost.getClassName()} pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
#foreach( $field in ${pojoHost.getFields()} )
		map.put("${field.getName()}", pojo.get${field.getName()}());
#end

		return map;
	}
}
