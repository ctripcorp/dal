package ${host.getNamespace()};

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

public class ${host.getPojoClassName()}Dao {
	private DalTableDao<${host.getPojoClassName()}> client = new DalTableDao<${host.getPojoClassName()}>(new ${host.getPojoClassName()}Parser());

	public ${host.getPojoClassName()} queryByPk(Number id, DalHints hints)
			throws SQLException {
		return client.queryByPk(id, hints);
	}

	public ${host.getPojoClassName()} queryByPk(${host.getPojoClassName()} pk, DalHints hints)
			throws SQLException {
		return client.queryByPk(pk, hints);
	}
	
	public List<${host.getPojoClassName()}> queryByPage(${host.getPojoClassName()} pk, int pageSize, int pageNo, DalHints hints)
			throws SQLException {
		// TODO to be implemented
		return null;
	}
	
	public void insert(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		client.insert(hints, null, daoPojos);
	}

	public void insert(DalHints hints, KeyHolder keyHolder, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		client.insert(hints, keyHolder, daoPojos);
	}
	
	public void delete(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		client.delete(hints, daoPojos);
	}
	
	public void update(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		client.update(hints, daoPojos);
	}

	#foreach( $method in $host.getMethods() )
	#set($parameters = $method.getParameters())
public #if( $method.getAction() == "select" )ResultSet#{else}int#end ${method.getMethodName()}#[[(]]##foreach($p in $parameters)${p.getJavaClass().getSimpleName()} ${p.getName()}#if($foreach.count != $parameters.size()), #end#end#[[)]]# {
		StatementParameters parameters = new StatementParameters();
		#foreach($p in $parameters)
		parameters.set(${p.getIndex()}, ${p.getSqlType()}, ${p.getName()});
		#end
return this.#if( $method.getAction() == "select" )fetch#{else}execute#end("${method.getSpName()}", parameters, null);
	}

	#end

	private static class ${host.getPojoClassName()}Parser implements DalParser<${host.getPojoClassName()}> {
		public static final String DATABASE_NAME = "${host.getDb_name()}";
		public static final String TABLE_NAME = "${host.getTable_name()}";
		private static final String[] COLUMNS = new String[]{
#foreach( $field in ${host.getFields()} )
			"${field.getName()}",
#end
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
#foreach( $field in ${host.getFields()} )
#if($field.isPrimary())
			"${field.getName()}",
#end
#end
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
#foreach( $field in ${host.getFields()} )
			${field.getDataType()},
#end
		};
		
		@Override
		public ${host.getPojoClassName()} map(ResultSet rs, int rowNum) throws SQLException {
			${host.getPojoClassName()} pojo = new ${host.getPojoClassName()}();
			
#foreach( $field in ${host.getFields()} )
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
			return ${host.isHasIdentity()};
		}
	
		@Override
		public Number getIdentityValue(${host.getPojoClassName()} pojo) {
			return #if($host.isHasIdentity())pojo.get${host.getIdentityColumnName()}()#{else}null#end;
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(${host.getPojoClassName()} pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
#foreach( $field in ${host.getFields()} )
#if($field.isPrimary())
			primaryKeys.put("${field.getName()}", pojo.get${field.getName()}());
#end
#end
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(${host.getPojoClassName()} pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
#foreach( $field in ${host.getFields()} )
			map.put("${field.getName()}", pojo.get${field.getName()}());
#end
	
			return map;
		}
	}
}
