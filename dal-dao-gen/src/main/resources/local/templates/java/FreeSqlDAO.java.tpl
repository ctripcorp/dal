package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

public class ${host.getClassName()}Dao {
	private static final String DATA_BASE = "${host.getDbName()}";
	private DalQueryDao queryDao;

#foreach( $method in ${host.getMethods()} )
#if(!$method.isEmptyFields())
	private ${method.getPojoClassName()}RowMapper ${method.getVariableName()}RowMapper = new ${method.getPojoClassName()}RowMapper();
#end
#end
	public ${host.getClassName()}Dao() {
		queryDao = new DalQueryDao(DATA_BASE);
	}
    
#foreach($method in $host.getMethods())
	public List<${method.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();

		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
#end

		//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
		return queryDao.query(sql, parameters, hints, ${method.getVariableName()}RowMapper);
	}

#end

#foreach( $method in ${host.getMethods()} )
#if(!$method.isEmptyFields())
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
