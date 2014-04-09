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
		
			//Query by perimary key
			${host.getPojoClassName()} pk = dao.queryByPk(0);
			
			//Query by the Pojo which contains perimary key
			pk = dao.queryByPk(pk); 
			
			//Invoke the paging function
			//Note that both the pageSize and the pageNumber must be greater than 1
			List<${host.getPojoClassName()}> pojos = dao.queryByPage(pk, 100, 1);
			
			//Get all records
			List<${host.getPojoClassName()}> all = dao.getAll();

#if($host.getSpInsert().isExist())
			${host.getPojoClassName()} pojo = new ${host.getPojoClassName()}(); 
			//Set fields here
			
			dao.insert(pojo);
#else
		    ${host.getPojoClassName()} pojo1 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo2 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo3 = new ${host.getPojoClassName()}();			
			//Set fields for pojos here
			
			//keyHolder will pull back the auto-increament keys
			dao.insert(new KeyHolder(), pojo1, pojo2, pojo3);
#end
#if($host.getSpUpdate().isExist())
			//Make some change to the pojo. set primary key
			dao.update(pojo);
#else
		    //Make some change to the pojo1. set primary key
			dao.update(pojo1);
#end
#if($host.getSpDelete().isExist())
			//Remove the pojo according to its primary keys
			dao.delete(pojo);
#else
	        //Remove the pojos according to its primary keys
		    dao.delete(pojo1, pojo2, pojo3);
#end

	        //Get the count
			int count = dao.count();
#set($count = 0)
#if($host.hasMethods())
			// Test additional customized method
			int affectedRows = 0;
			List<${host.getPojoClassName()}> results = null;
#foreach($method in $host.getMethods())
#set($count = $count+1)
#set($suffix = $count+'')
			// Test ${method.getName()}
#foreach($p in $method.getParameters())
			${p.getClassDisplayName()} ${p.getName()}${suffix} = null; //set you value here
#end

#if($method.getCrud_type() == "select")
		    results = dao.${method.getName()}(${method.getParameterNames($suffix)});

#else
    		affectedRows = dao.${method.getName()}(${method.getParameterNames($suffix)});

#end
#end
#end
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}