//${host.getProjectName()}
package ${host.getPackageName()}.dao;

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end
import ${host.getPackageName()}.entity.${host.getPojoClassName()};

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class ${host.getPojoClassName()}Dao {
	private static final boolean ASC = true;
	private DalTableDao<${host.getPojoClassName()}> client;
	
#parse("templates/java/dao/standard/method.constructor.tpl")
#parse("templates/java/dao/standard/method.queryByPk.tpl")
#parse("templates/java/dao/standard/method.count.tpl")
#parse("templates/java/dao/standard/method.queryByPage.tpl")
#parse("templates/java/dao/standard/method.getAll.tpl")
#parse("templates/java/dao/standard/method.Insert.notSp.tpl")
#parse("templates/java/dao/standard/method.Delete.notSp.tpl")
#parse("templates/java/dao/standard/method.Update.notSp.tpl")
#parse("templates/java/dao/autosql/DAO.java.tpl")
}