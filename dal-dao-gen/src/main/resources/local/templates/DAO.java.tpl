package ${host.getNamespace()};

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

#foreach( $field in ${host.getImports()} )
import ${field};
#end

public class ${host.getPojoClassName()}Dao {
	private DalTableDao<${host.getPojoClassName()}> client = new DalTableDao<${host.getPojoClassName()}>(new ${host.getPojoClassName()}Parser());
	private DalClient baseClient = DalClientFactory.getClient(parser.getDatabaseName());

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

#foreach($method in $host.getExtraMethods())
    public #if($method.getCrud_type() == "select")List<${host.getClassName()}>#{else}int#end ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
#foreach($p in $method.getParameters())  
		parameters.set(i++, ${p.getSqlType()}, ${p.getName());
#end
#if($method.getCrud_type() == "select")
		return queryDao.query(sql, parameters, hints, personRowMapper);
#else
		return baseClient.update(sql, parameters, hint);
#end
	}
#end

	private static class ${host.getPojoClassName()}Parser implements DalParser<${host.getPojoClassName()}> {
		public static final String DATABASE_NAME = "${host.getDbName()}";
		public static final String TABLE_NAME = "${host.getTableName()}";
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
			${field.getSqlType()},
#end
		};
		
		@Override
		public ${host.getPojoClassName()} map(ResultSet rs, int rowNum) throws SQLException {
			${host.getPojoClassName()} pojo = new ${host.getPojoClassName()}();
			
#foreach( $field in ${host.getFields()} )
			pojo.set${field.getName()}((${field.getJavaClass().getSimpleName()})rs.getObject("${field.getName()}"));
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
