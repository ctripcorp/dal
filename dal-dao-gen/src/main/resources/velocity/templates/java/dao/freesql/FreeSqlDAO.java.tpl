package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.helper.*;

public class ${host.getClassName()}Dao {

	private static final String DATA_BASE = "${host.getDbSetName()}";
	
#if($host.hasQuery())
	private DalQueryDao queryDao = null;
#end

#foreach( $method in ${host.getMethods()} )
#if(!$method.isEmptyFields() && !$method.isSampleType())
	private DalRowMapper<${method.getPojoClassName()}> ${method.getVariableName()}RowMapper = null;
#end

#end
	public ${host.getClassName()}Dao() throws SQLException {
#foreach( $method in ${host.getMethods()} )
#if(!$method.isEmptyFields() && !$method.isSampleType())
		this.${method.getVariableName()}RowMapper = new DalDefaultJpaMapper(${method.getPojoClassName()}.class);
#end
#end	
#if($host.hasQuery())
		this.queryDao = new DalQueryDao(DATA_BASE);
#end
	}
#parse("templates/java/dao/freesql/method.scalar.Simple.List.tpl")
#parse("templates/java/dao/freesql/method.scalar.Simple.Single.tpl")
#parse("templates/java/dao/freesql/method.scalar.Simple.First.tpl")
#parse("templates/java/dao/freesql/method.scalar.Entity.List.tpl")
#parse("templates/java/dao/freesql/method.scalar.Entity.Single.tpl")
#parse("templates/java/dao/freesql/method.scalar.Entity.First.tpl")
#parse("templates/java/dao/freesql/method.cud.tpl")

}
