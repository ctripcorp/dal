
package ${pojoHost.getDaoNamespace()};

#foreach( $field in ${pojoHost.getImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalPojo;

public class ${pojoHost.getClassName()} implements DalPojo {

#foreach( $field in ${pojoHost.getFields()} )
	private ${field.getJavaClass().getSimpleName()} ${field.getName()};
#end

#foreach( $field in ${pojoHost.getFields()} )
	public ${field.getJavaClass().getSimpleName()} get${field.getName()}#[[(){]]#
		return ${field.getName()};
	#[[}]]#

	public void set${field.getName()}#[[(]]#${field.getJavaClass().getSimpleName()} ${field.getName()}#[[)]]##[[{]]#
		this.${field.getName()} = ${field.getName()};
	#[[}]]#

#end
}