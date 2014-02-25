package $namespace;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

public class $dao_name {
	private DalTableDao<Person> client = new DalTableDao<Person>(new PersonParser());

	public Person queryByPk(Number id, DalHints hints)
			throws SQLException {
		return client.queryByPk(id, hints);
	}

	public Person queryByPk(Person pk, DalHints hints)
			throws SQLException {
		return client.queryByPk(pk, hints);
	}
	
	public List<Person> queryByPage(Person pk, int pageSize, int pageNo, DalHints hints)
			throws SQLException {
		// TODO to be implemented
		return null;
	}
	
	public void insert(DalHints hints, Person...daoPojos) throws SQLException {
		client.insert(hints, null, daoPojos);
	}

	public void insert(DalHints hints, KeyHolder keyHolder, Person...daoPojos) throws SQLException {
		client.insert(hints, keyHolder, daoPojos);
	}
	
	public void delete(DalHints hints, Person...daoPojos) throws SQLException {
		client.delete(hints, daoPojos);
	}
	
	public void update(DalHints hints, Person...daoPojos) throws SQLException {
		client.update(hints, daoPojos);
	}

	#foreach( $method in $methods )
	#set($parameters = $method.getParameters())
public #if( $method.getAction() == "select" )ResultSet#{else}int#end ${method.getMethodName()}#[[(]]##foreach($p in $parameters)${p.getType()} ${p.getName()}#if($foreach.count != $parameters.size()), #end#end#[[)]]# {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		#foreach($p in $parameters)
parameters.add(StatementParameter.newBuilder().setDbType(DbType.${JavaDbTypeMap.get($p.getType())}).setDirection(ParameterDirection.Input).setNullable(false).setIndex(${p.getPosition()}).setName("").setSensitive(false).setValue(${p.getName()}).build());
		#end
return this.#if( $method.getAction() == "select" )fetch#{else}execute#end("${method.getSqlSPName()}", parameters, null);
	}

	#end

	#foreach( $method in $sp_methods )
	#set($parameters = $method.getParameters())
public int ${method.getMethodName()}#[[(]]##foreach($p in $parameters)${p.getType()} ${p.getName()}#if($foreach.count != $parameters.size()), #end#end#[[)]]# {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		#foreach($p in $parameters)
parameters.add(StatementParameter.newBuilder().setDbType(DbType.${JavaDbTypeMap.get($p.getType())}).setDirection(#if( $p.getParamMode() == "OUT" )ParameterDirection.InputOutput#{else}ParameterDirection.Input#end).setNullable(false).setIndex(${p.getPosition()}).setName("").setSensitive(false).setValue(${p.getName()}).build());

		#end
return this.executeSP("${method.getSqlSPName()}", parameters, null);
	}

	#end

	private static class ${parserHost.getClassName()} implements DalParser<${pojoHost.getClassName()}> {
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
			${pojoHost.getClassName()} pojo = new ${pojoHost.getClassName()}();
			
#foreach( $field in ${pojoHost.getFields()} )
			pojo.set${field.getName()}((${field.getJavaClass().getSimpleName()})rs.getObject("${field.getName()}"));
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
		public Number getIdentityValue(${pojoHost.getClassName()} pojo) {
			return #if($parserHost.isHasIdentity())pojo.get${parserHost.getIdentityColumnName()}()#{else}null#end;
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(${pojoHost.getClassName()} pojo) {
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
}
