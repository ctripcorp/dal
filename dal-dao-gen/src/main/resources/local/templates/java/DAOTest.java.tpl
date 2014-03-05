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
			
			// test insert
			${host.getPojoClassName()} pojo1 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo2 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo3 = new ${host.getPojoClassName()}();
			pojos.add(pojo1, pojo2, pojo3);
			
			KeyHolder keyHolder = new KeyHolder();
			${host.getPojoClassName()} pojo4 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo5 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo6 = new ${host.getPojoClassName()}();
			dao.insert(keyHolder, pojo4, pojo5, pojo6);
			
			dao.delete(pojo1, pojo2, pojo3);
			dao.update(pojo4, pojo5, pojo6);
			Map<String, ?> result = dao.call${host.getPojoClassName()}(param);

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
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

}