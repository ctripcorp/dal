package ${host.getPackageName()}.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;
import java.sql.Types;
#foreach( $field in ${host.getPojoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalPojo;

@Entity
@Database(name="$!{host.getDbSetName()}")
@Table(name="$!{host.getTableName()}")
public class ${host.getPojoClassName()} implements DalPojo {
#foreach( $field in ${host.getFields()} )

#if($field.getComment() != "")
    //$field.getComment()
#end
#if(${field.isPrimary()})
	@Id
#end
	@Column(name="${field.getName()}"#if($field.isDataChangeLastTimeField()), insertable=false, updatable=false#end#if($field.isStringType() && $host.getLength()), length = ${field.getLength()}#end)
#if(${field.isIdentity()})
	@GeneratedValue(strategy = GenerationType.AUTO)
#end
	@Type(value=${field.getJavaTypeDisplay()})
	private ${field.getClassDisplayName()} ${field.getCamelCaseUncapitalizedName()};
#end

#foreach( $field in ${host.getFields()} )
	public ${field.getClassDisplayName()} get${field.getCamelCaseCapitalizedName()}() {
		return ${field.getCamelCaseUncapitalizedName()};
	}

	public void set${field.getCamelCaseCapitalizedName()}(${field.getClassDisplayName()} ${field.getCamelCaseUncapitalizedName()}) {
		this.${field.getCamelCaseUncapitalizedName()} = ${field.getCamelCaseUncapitalizedName()};
	}

#end
}