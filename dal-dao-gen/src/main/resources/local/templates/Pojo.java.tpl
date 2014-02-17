
package $namespace;

import com.ctrip.platform.dal.dao.DalPojo;

public class $table_name implements DalPojo {
#foreach( $field in $fields )
	private ${field.getType()} ${field.getName()};
#end

#foreach( $field in $fields )
	public ${field.getType()} get${field.getName()}#[[(){]]#
		return ${field.getName()};
	#[[}]]#

	public void set${field.getName()}#[[(]]#${field.getType()} ${field.getName()}#[[)]]##[[{]]#
		this.${field.getName()} = ${field.getName()};
	#[[}]]#

#end
}