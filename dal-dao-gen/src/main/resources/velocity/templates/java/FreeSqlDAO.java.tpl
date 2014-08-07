package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

public class ${host.getClassName()}Dao {
	private static final String DATA_BASE = "${host.getDbName()}";
	
#if($host.hasQuery())
	private DalQueryDao queryDao;
#end
#if($host.hasUpdate())
	private DalClient baseClient;
#end

#foreach( $method in ${host.getMethods()} )
#if(!$method.isEmptyFields() && !$method.isSampleType() && $method.isQuery())
	private ${method.getPojoClassName()}RowMapper ${method.getVariableName()}RowMapper = new ${method.getPojoClassName()}RowMapper();
#end
#end
	public ${host.getClassName()}Dao() {
#if($host.hasQuery())
		this.queryDao = new DalQueryDao(DATA_BASE);
#end
#if($host.hasUpdate())
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
#end
	}
    
#foreach($method in $host.getMethods())
		/**
		 * ${method.getComments()}
		**/
#if($method.getCrud_type()=="select")
##简单类型并且返回值是List
#if($method.isSampleType() && $method.isReturnList())
	public List<${method.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {	
#if($method.isPaging())
		String sqlPattern = "${method.getPagingSql($host.getDatabaseCategory())}";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
#else
		String sql = "${method.getSql()}";
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#end
		return queryDao.query(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
##简单类型并且返回值为Single
#if($method.isSampleType() && $method.isReturnSingle())
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#end
		return queryDao.queryForObjectNullable(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
##简单类型且返回值为First
#if($method.isSampleType() && $method.isReturnFirst())
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#end
		return queryDao.queryFirstNullable(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
##实体类型并且返回值为List
#if($method.isReturnList() && !$method.isSampleType())
	public List<${method.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
#if($method.isPaging())
		String sqlPattern = "${method.getPagingSql($host.getDatabaseCategory())}";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
#else
		String sql = "${method.getSql()}";
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#end
		return queryDao.query(sql, parameters, hints, ${method.getVariableName()}RowMapper);
	}
#end
##实体类型且返回Signle
#if($method.isReturnSingle() && !$method.isSampleType())
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#end
		return queryDao.queryForObjectNullable(sql, parameters, hints, ${method.getVariableName()}RowMapper);
	}
#end
##实体类型且返回First
#if($method.isReturnFirst() && !$method.isSampleType())
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#end
		return queryDao.queryFirstNullable(sql, parameters, hints, ${method.getVariableName()}RowMapper);
	}
#end
#else
	public int ${method.getName()} (${method.getParameterDeclaration()}) throws SQLException {
#if(${method.getInClauses()} != "")
		String sql = SQLParser.parse("${method.getSql()}",${method.getInClauses()});
#else
		String sql = SQLParser.parse("${method.getSql()}");
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		
		int i = 1;
#foreach($p in $method.getParameters())
#if($p.isInParameter())
		i = parameters.setInParameter(i, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#else
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
		return baseClient.update(sql, parameters, hints);
	}
#end

#end

#foreach( $method in ${host.getMethods()} )
#if(!$method.isEmptyFields()&& !$method.isSampleType())
	private class ${method.getPojoClassName()}RowMapper implements DalRowMapper<${method.getPojoClassName()}> {

		@Override
		public ${method.getPojoClassName()} map(ResultSet rs, int rowNum) throws SQLException {
			${method.getPojoClassName()} pojo = new ${method.getPojoClassName()}();
			
#foreach( $field in ${method.getFields()} )
			pojo.set${field.getCapitalizedName()}((${field.getClassDisplayName()})rs.getObject("${field.getName()}"));
#end

			return pojo;
		}
	}
#end
#end
}
