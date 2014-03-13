package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.*;

public class ${host.getPojoClassName()}DaoTest {
	public static void main(String[] args) {
		
		try {
			// Initialize DalClientFactory
			DalClientFactory.initClientFactory("${host.getDbName()}");
			${host.getPojoClassName()}Dao dao = new ${host.getPojoClassName()}Dao();
		
#if($host.isHasIdentity())
			${host.getPojoClassName()} pk = dao.queryByPk(null);// you value here
#end
			
			pk = dao.queryByPk(pk);
			List<${host.getPojoClassName()}> pojos = dao.queryByPage(pk, 100, 0);
			
#if($host.getSpaInsert().isExist() == false)
			// Test SPA related CUD
			// test insert
			${host.getPojoClassName()} pojo = new ${host.getPojoClassName()}();
			// Set pojo attribute value here
			dao.insert(pojo1);
			// make some change to the pojo. set primary key
			dao.update(pojo);
			
			// remove the pojo
			dao.delete(pojo);
#else
			// Test normal CUD (non SPA) 
			KeyHolder keyHolder = new KeyHolder();
			${host.getPojoClassName()} pojo1 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo2 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo3 = new ${host.getPojoClassName()}();
			dao.insert(keyHolder, pojo1, pojo2, pojo3);
			dao.delete(pojo1, pojo2, pojo3);
			dao.update(pojo1, pojo2, pojo3);
#end
			// Test additional customized method
			int affectedRows = 0;
			List<${host.getPojoClassName()}> results = null;
#foreach($method in $host.getMethods())
			// Test ${method.getName()}
#foreach($p in $method.getParameters())  
			${p.getClassDisplayName()} ${p.getName()} = ${p.getValidationValue()};
#end
#if($method.getCrud_type() == "select")
		    results = dao.${method.getName()}($method.getParameterNames()});

#else
    		affectedRows = dao.${method.getName()}($method.getParameterNames()});

#end
#end
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}