package ${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.*;

public class ${host.getClassName()}DaoTest {
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
			
			${host.getClassName()}Dao dao = new ${host.getClassName()}Dao();
			
#set($count = 0)
#foreach( $method in ${host.getMethods()} )
#set($count = $count+1)
#set($suffix = $count+'')

			// Test ${method.getName()}: ${method.getComments()}
#foreach($p in $method.getParameters())
			${p.getClassDisplayName()} ${p.getAlias()}${suffix} = ${p.getValidationValue()};// Test value here
#end
#if($method.isQuery())
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
		    List<${method.getPojoClassName()}> results${suffix} = dao.${method.getName()}(${method.getParameterNames($suffix)});
			System.out.println(results${suffix}.size());
#else
		    ${method.getPojoClassName()} results${suffix} = dao.${method.getName()}(${method.getParameterNames($suffix)});
			System.out.println(results${suffix});
#end
#end
#else
		    int results${suffix} = dao.${method.getName()}(${method.getParameterNames($suffix)});
			System.out.println(results${suffix});
#end
#end
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
