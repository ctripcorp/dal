package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.helper.*;

public class ${host.getClassName()}Dao {
	private static final String DATA_BASE = "${host.getDbSetName()}";
	
#if($host.hasQuery())
	private DalQueryDao queryDao;
#end
#if($host.hasUpdate())
	private DalClient baseClient;
#end

#foreach( $method in ${host.getMethods()} )
#if(!$method.isEmptyFields() && !$method.isSampleType())
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

#parse("templates/java/dao/freesql/method.scalar.Simple.List.tpl")
#parse("templates/java/dao/freesql/method.scalar.Simple.Single.tpl")
#parse("templates/java/dao/freesql/method.scalar.Simple.First.tpl")
#parse("templates/java/dao/freesql/method.scalar.Entity.List.tpl")
#parse("templates/java/dao/freesql/method.scalar.Entity.Single.tpl")
#parse("templates/java/dao/freesql/method.scalar.Entity.First.tpl")
#parse("templates/java/dao/freesql/method.cud.tpl")

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
