package com.ctrip.platform.dal.daogen.host.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.ctrip.platform.dal.daogen.enums.ConditionType;

public class JavaMethodHost {
	private String crud_type;
	private String name;
	private String sql;
	private String packageName;
	// DAO class name
	private String className;
	private String pojoClassName;
	private List<JavaParameterHost> parameters;
	// Only for free sql query dao
	private List<JavaParameterHost> fields;
	
	private String scalarType;
	private String pojoType;
	
	private List<String> inClauses = new ArrayList<String>();

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPojoClassName() {
		if(this.isSampleType()){
			if(null != this.fields && !this.fields.isEmpty()){
				return this.fields.get(0).getClassDisplayName();
			}
		}
		return pojoClassName;
	}

	public void setPojoClassName(String pojoClassName) {
		this.pojoClassName = pojoClassName;
	}

	public List<JavaParameterHost> getFields() {
		return fields;
	}

	public void setFields(List<JavaParameterHost> fields) {
		this.fields = fields;
	}

	public String getCrud_type() {
		return crud_type;
	}

	public void setCrud_type(String crud_type) {
		this.crud_type = crud_type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScalarType() {
		return scalarType;
	}

	public void setScalarType(String scalarType) {
		this.scalarType = scalarType;
	}

	public String getPojoType() {
		if(null != this.pojoType)
			return this.pojoType;
		if(null != this.fields && this.fields.size() == 1){
			return "SimpleType";
		}else{
			return "";
		}
	}

	public void setPojoType(String pojoType) {
		this.pojoType = pojoType;
	}

	public String getSql() {
		String newSql = sql.replaceAll("[\\n\\r]", " ");
		return newSql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<JavaParameterHost> getParameters() {
		return parameters;
	}

	public void setParameters(List<JavaParameterHost> parameters) {
		this.parameters = parameters;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getVariableName() {
		return WordUtils.uncapitalize(pojoClassName);
	}
	
	public String getInClauses()
	{
		return StringUtils.join(this.inClauses, ",");
	}
	
	public String getParameterNames() {
		String[] params = new String[parameters.size()];
		int i = 0;
		for(JavaParameterHost parameter: parameters) {
			params[i++] = parameter.getName();
		}
		
		return StringUtils.join(params, ", ");
	}
	
	public String getParameterNames(String suffix) {
		String[] params = new String[parameters.size()];
		int i = 0;
		for(JavaParameterHost parameter: parameters) {
			params[i++] = parameter.getName() + (null != suffix ? suffix : "");
		}
		
		return StringUtils.join(params, ", ");
	}
	
	public String getParameterDeclaration() {
		String[] paramsDeclaration = new String[parameters.size()];
		int i = 0;
		for(JavaParameterHost parameter: parameters) {
			if(ConditionType.In == parameter.getConditionType()){
				paramsDeclaration[i++] = String.format("List<%s> %s", parameter.getClassDisplayName(), parameter.getAlias());
				this.inClauses.add(parameter.getAlias());
			}
			else
				paramsDeclaration[i++] = String.format("%s %s", parameter.getClassDisplayName(), parameter.getAlias());
		}
		
		return StringUtils.join(paramsDeclaration, ", ");
	}

	public Set<String> getPojoImports() {
		Set<String> imports = new TreeSet<String>();

		List<JavaParameterHost> allTypes = new ArrayList<JavaParameterHost>(fields);
		for(JavaParameterHost field: allTypes) {
			Class<?> clazz = field.getJavaClass();
			if(byte[].class.equals(clazz))
				continue;
			if(clazz.getPackage().getName().equals(String.class.getPackage().getName()))
				continue;
			imports.add(clazz.getName());
		}
		return imports;
	}
	
	public List<String> getParamComments()
	{
		List<String> params = new ArrayList<String>();
		for(JavaParameterHost parameter: parameters) {
			if(!parameter.isConditional())
				params.add(parameter.getAlias() + ": set clause");
		}
		return params;
	}
	
	public List<String> getConditionComments()
	{
		List<String> params = new ArrayList<String>();
		for(JavaParameterHost parameter: parameters) {
			if(parameter.isConditional())
				params.add(parameter.getAlias() + ": where clause");
		}
		return params;
	}
	
	public boolean hasParameters(){
		return null != this.parameters && !this.parameters.isEmpty();
	}
	
	public boolean isEmptyFields()
	{
		return null == this.fields || this.fields.isEmpty();
	}
	
	public boolean isSampleType(){
		return this.getPojoType().equalsIgnoreCase("SimpleType");
	}
	
	public boolean isReturnList(){
		return this.scalarType.equalsIgnoreCase("List");
	}
	
	public boolean isReturnSingle(){
		return this.scalarType.equalsIgnoreCase("Single");
	}
	
	public boolean isReturnFirst(){
		return this.scalarType.equalsIgnoreCase("First");
	}
	
}