package ${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClientFactory;

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
			
			//Get the count
			System.out.println(dao.Count());
			
			//Get all records
			List<${host.getPojoClassName()}> ls = dao.getAll();
			if(null != ls)
				System.out.println(ls.size());
				
			List<${host.getPojoClassName()}> lsp = dao.getListByPage(100, 1);
			if(null != lsp)
				System.out.println(ls.size());
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}