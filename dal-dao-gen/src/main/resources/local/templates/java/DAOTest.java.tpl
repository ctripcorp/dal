package ${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.*;

public class ${host.getPojoClassName()}DaoTest {
	public static void main(String[] args) {
		
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of follow three need to be enabled.
			**/
			//DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
			DalClientFactory.initClientFactory(); // load from class-path Dal.config
			//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
			
			${host.getPojoClassName()}Dao dao = new ${host.getPojoClassName()}Dao();
		
			${host.getPojoClassName()} pk = dao.queryByPk(0);// you value here
			
			pk = dao.queryByPk(pk);
			List<${host.getPojoClassName()}> pojos = dao.queryByPage(pk, 100, 0);

#if($host.isSpa())
#if($host.getSpInsert().isExist())
			// Test SPA related CUD
			// test insert
			${host.getPojoClassName()} pojo = new ${host.getPojoClassName()}();
			// Set pojo attribute value here
			dao.insert(pojo);
	
#end
#if($host.getSpUpdate().isExist())
			// make some change to the pojo. set primary key
			dao.update(pojo);
#end

#if($host.getSpDelete().isExist())
			// remove the pojo
			dao.delete(pojo);
#end
	
#else
#if($host.getSpInsert().isExist())
			// Test normal CUD (non SPA) 
			KeyHolder keyHolder = new KeyHolder();
			${host.getPojoClassName()} pojo1 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo2 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo3 = new ${host.getPojoClassName()}();
			dao.insert(keyHolder, pojo1, pojo2, pojo3);
	
#end
#if($host.getSpUpdate().isExist())
			// make some change to the pojo. set primary key
			dao.update(pojo1, pojo2, pojo3);
#end

#if($host.getSpDelete().isExist())
			// remove the pojo
			dao.delete(pojo1, pojo2, pojo3);
#end			
#end

#if($host.hasMethods())
			// Test additional customized method
			int affectedRows = 0;
			List<${host.getPojoClassName()}> results = null;
#foreach($method in $host.getMethods())
			// Test ${method.getName()}
#foreach($p in $method.getParameters())  
			${p.getClassDisplayName()} ${p.getName()} = null; //set you value here
#end

#if($method.getCrud_type() == "select")
		    results = dao.${method.getName()}(${method.getParameterNames()});

#else
    		affectedRows = dao.${method.getName()}(${method.getParameterNames()});

#end
#end
#end
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}