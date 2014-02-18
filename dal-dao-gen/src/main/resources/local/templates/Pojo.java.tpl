
package ${pojoHost.getDaoNamespace()};

import com.ctrip.platform.dal.dao.DalPojo;

public class ${pojoHost.getClassName()} implements DalPojo {

#foreach( $field in ${pojoHost.getFields()} )
	private ${field.getType()} ${field.getName()};
#end

#foreach( $field in ${pojoHost.getFields()} )
	public ${field.getType()} get${field.getName()}#[[(){]]#
		return ${field.getName()};
	#[[}]]#

	public void set${field.getName()}#[[(]]#${field.getType()} ${field.getName()}#[[)]]##[[{]]#
		this.${field.getName()} = ${field.getName()};
	#[[}]]#

#end

}