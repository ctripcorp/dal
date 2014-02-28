package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.*;

public class ${host.getPojoClassName()}Dao {
	private DalTableDao<${host.getPojoClassName()}> client = new DalTableDao<${host.getPojoClassName()}>(new ${host.getPojoClassName()}Parser());
	private DalClient baseClient = DalClientFactory.getClient(host.getDbName());

#if($host.isHasIdentity())
	public ${host.getPojoClassName()} queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}
#end

	public ${host.getPojoClassName()} queryByPk(${host.getPojoClassName()} pk)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(pk, hints);
	}
	
	// TODO add query by PK column list
	
	public List<${host.getPojoClassName()}> queryByPage(${host.getPojoClassName()} pk, int pageSize, int pageNo)
			throws SQLException {
		// TODO to be implemented
		DalHints hints = new DalHints();
		return null;
	}
	
	public void insert(${host.getPojoClassName()}...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.insert(hints, null, daoPojos);
	}

	public void insert(KeyHolder keyHolder, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.insert(hints, keyHolder, daoPojos);
	}
	
	public void delete(${host.getPojoClassName()}...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.delete(hints, daoPojos);
	}
	
	public void update(${host.getPojoClassName()}...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.update(hints, daoPojos);
	}

#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
    public List<${host.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) 
			throws SQLException {
#else
    public int ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
#end
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
#foreach($p in $method.getParameters())  
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
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
			${field.getJavaTypeDisplay()},
#end
		};
		
		@Override
		public ${host.getPojoClassName()} map(ResultSet rs, int rowNum) throws SQLException {
			${host.getPojoClassName()} pojo = new ${host.getPojoClassName()}();
			
#foreach( $field in ${host.getFields()} )
			pojo.set${field.getCapitalizedName()}((${field.getClassDisplayName()})rs.getObject("${field.getName()}"));
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
			return #if($host.isHasIdentity())pojo.get${WordUtils.capitalized(${host.getIdentityColumnName()})}()#{else}null#end;
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(${host.getPojoClassName()} pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
#foreach( $field in ${host.getFields()} )
#if($field.isPrimary())
			primaryKeys.put("${field.getName()}", pojo.get${field.getCapitalizedName()}());
#end
#end
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(${host.getPojoClassName()} pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
#foreach( $field in ${host.getFields()} )
			map.put("${field.getName()}", pojo.get${field.getCapitalizedName()}());
#end
	
			return map;
		}
	}
}