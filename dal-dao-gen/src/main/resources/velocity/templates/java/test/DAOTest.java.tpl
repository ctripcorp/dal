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
#if($host.hasPk())
#foreach($p in $host.getPrimaryKeys())
		    ${p.getClassDisplayName()} ${p.getUncapitalizedName()} = null;
#end
#end
			${host.getPojoClassName()} pk = dao.queryByPk(${host.getPkParametersList()});
			
			//Query by the Pojo which contains perimary key
			pk = dao.queryByPk(pk, new DalHints()); 
			
			//Invoke the paging function
			//Note that both the pageSize and the pageNumber must be greater than 1
			List<${host.getPojoClassName()}> pojos = dao.queryByPage(100, 1, new DalHints());
			
			//Get all records
			List<${host.getPojoClassName()}> all = dao.getAll(new DalHints());

#if($host.getSpInsert().isExist())
			${host.getPojoClassName()} pojo = new ${host.getPojoClassName()}(); 
			//Set fields here
			
			dao.insert(null,pojo);
#else
		    ${host.getPojoClassName()} pojo1 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo2 = new ${host.getPojoClassName()}();
			${host.getPojoClassName()} pojo3 = new ${host.getPojoClassName()}();			
			//Set fields for pojos here
			KeyHolder kh = new KeyHolder();
			//keyHolder will pull back the auto-increament keys
			dao.insert(new DalHints(), kh, pojo1, pojo2, pojo3);
			
			Number generatedKey = kh.getKey(0);
			System.out.println(generatedKey);
#end
#if($host.getSpUpdate().isExist())
			//Make some change to the pojo. set primary key
			dao.update(new DalHints(), pojo);
#else
		    //Make some change to the pojo1. set primary key
			dao.update(new DalHints(), pojo1);
#end
#if($host.getSpDelete().isExist())
			//Remove the pojo according to its primary keys
			dao.delete(new DalHints(), pojo);
#else
	        //Remove the pojos according to its primary keys
		    dao.delete(new DalHints(), pojo1, pojo2, pojo3);
#end

	        //Get the count
			int count = dao.count(new DalHints());
			System.out.println(count);
			
#set($count = 0)
#if($host.hasMethods())
			// Test additional customized method
#foreach($method in $host.getMethods())
#set($count = $count+1)
#set($suffix = $count+'')
			// Test ${method.getName()}: ${method.getComments()}
#if($method.getCrud_type() == "select")	
#foreach($p in $method.getParameters())
			${p.getClassDisplayName()} ${p.getAlias()}${suffix} = null; //set you value here
#end
#if($method.isSampleType())
#if($method.isReturnList())
			List<${method.getPojoClassName()}> results${suffix} = dao.${method.getName()}(${method.getParameterNames($suffix)});
			System.out.println(results${suffix}.size());
#else
			${method.getPojoClassName()} results${suffix} = dao.${method.getName()}(${method.getParameterNames($suffix)});
			System.out.println(results${suffix});
#end
#else
#if($method.isReturnList())
			List<${host.getPojoClassName()}> results${suffix} = dao.${method.getName()}(${method.getParameterNames($suffix)});
			System.out.println(results${suffix}.size());
#else
			${host.getPojoClassName()} results${suffix} = dao.${method.getName()}(${method.getParameterNames($suffix)});
			System.out.println(results${suffix});
#end
#end
#else
#foreach($p in $method.getParameters())
		    ${p.getClassDisplayName()} ${p.getAlias()}${suffix} = null; //set your value here
#end
			int affectedRows${suffix} = dao.${method.getName()}(${method.getParameterNames($suffix)});
			System.out.println(affectedRows${suffix});
#end

#end
#end
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}