package com.ctrip.platform.dal.daogen.host.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class JavaTableHost {
	private DatabaseCategory databaseCategory;
	private String packageName;
	private String dbName;
	private String tableName;
	private String pojoClassName;
	private List<JavaParameterHost> fields;
	private List<JavaParameterHost> primaryKeys;
	private boolean hasIdentity;
	private String identityColumnName;
	private boolean Sp;
	private SpOperationHost SpInsert;
	private SpOperationHost SpDelete;
	private SpOperationHost SpUpdate;
	private List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();
	
	private String api_list;
	
	public boolean generateAPI(Integer ...apiID){
		if(api_list==null || api_list.isEmpty() || api_list==""){
			return true;
		}
		for(int i=0;i<apiID.length;i++){
			if(api_list.indexOf("dal_api_"+apiID[i])>0){
				return true;
			}
		}
		return false;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isSp() {
		return Sp;
	}

	public void setSp(boolean isSp) {
		this.Sp = isSp;
	}

	public boolean isSpa() {
		return null != this.SpInsert && this.SpInsert.isExist();
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPojoClassName() {
		return pojoClassName;
	}

	public void setPojoClassName(String pojoClassName) {
		this.pojoClassName = pojoClassName;
	}

	public List<JavaMethodHost> getMethods() {
		return methods;
	}

	public void setMethods(List<JavaMethodHost> methods) {
		this.methods = methods;
	}

	public boolean hasMethods() {
		return this.methods != null && !this.methods.isEmpty();
	}

	public List<JavaParameterHost> getFields() {
		return fields;
	}

	public void setFields(List<JavaParameterHost> fields) {
		this.fields = fields;
	}

	public List<JavaParameterHost> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(List<JavaParameterHost> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public boolean isHasIdentity() {
		return hasIdentity;
	}

	public void setHasIdentity(boolean hasIdentity) {
		this.hasIdentity = hasIdentity;
	}

	public String getCapitalizedIdentityColumnName() {
		for (JavaParameterHost field : fields) {
			if (field.getName().equals(identityColumnName))
				return field.getCapitalizedName();
		}
		return null;
	}

	public String getIdentityColumnName() {
		return identityColumnName;
	}

	public void setIdentityColumnName(String identityColumnName) {
		this.identityColumnName = identityColumnName;
	}

	public SpOperationHost getSpInsert() {
		return SpInsert;
	}

	public void setSpInsert(SpOperationHost SpInsert) {
		this.SpInsert = SpInsert;
	}

	public SpOperationHost getSpDelete() {
		return SpDelete;
	}

	public void setSpDelete(SpOperationHost SpDelete) {
		this.SpDelete = SpDelete;
	}

	public SpOperationHost getSpUpdate() {
		return SpUpdate;
	}

	public void setSpUpdate(SpOperationHost SpUpdate) {
		this.SpUpdate = SpUpdate;
	}

	public DatabaseCategory getDatabaseCategory() {
		return databaseCategory;
	}

	public void setDatabaseCategory(DatabaseCategory databaseCategory) {
		this.databaseCategory = databaseCategory;
	}
	
	public String getApi_list() {
		return api_list;
	}

	public void setApi_list(String api_list) {
		this.api_list = api_list;
	}

	public boolean isIntegerPk() {
		return primaryKeys.size() == 1
				&& (primaryKeys.get(0).getJavaClass().equals(Integer.class) || primaryKeys
						.get(0).getJavaClass().equals(Long.class));
	}

	public String getPkParameterDeclaration() {
		List<String> paramsDeclaration = new ArrayList<String>();
		for (JavaParameterHost parameter : primaryKeys) {
			paramsDeclaration.add(String.format("%s %s",
					parameter.getClassDisplayName(),
					parameter.getUncapitalizedName()));
		}
		paramsDeclaration.add(String.format("%s %s", "DalHints", "hints"));
		return StringUtils.join(paramsDeclaration, ", ");
	}

	public String getPkParametersList() {
		List<String> paramsList = new ArrayList<String>();
		for (JavaParameterHost parameter : primaryKeys) {
			paramsList.add(parameter.getUncapitalizedName());
		}
		paramsList.add("new DalHints()");

		return StringUtils.join(paramsList, ",");
	}

	public boolean hasPk() {
		return null != primaryKeys && !primaryKeys.isEmpty();
	}

	public Set<String> getDaoImports() {
		Set<String> imports = new TreeSet<String>();
		imports.add("com.ctrip.platform.dal.dao.*");
		imports.add("com.ctrip.platform.dal.dao.helper.*");

		imports.add(java.sql.ResultSet.class.getName());
		imports.add(java.sql.SQLException.class.getName());
		imports.add(java.util.Map.class.getName());
		imports.add(java.util.LinkedHashMap.class.getName());
		imports.add(java.sql.Types.class.getName());
		imports.add(java.util.List.class.getName());

		List<JavaParameterHost> allTypes = new ArrayList<JavaParameterHost>(
				fields);
		for (JavaMethodHost method : methods) {
			allTypes.addAll(method.getParameters());
		}

		if (SpInsert != null)
			allTypes.addAll(SpInsert.getParameters());
		if (SpDelete != null)
			allTypes.addAll(SpDelete.getParameters());
		if (SpUpdate != null)
			allTypes.addAll(SpUpdate.getParameters());

		for (JavaParameterHost field : allTypes) {
			if (null != field.getDirection()
					&& (field.getDirection().name().equals("InputOutput") 
							|| field.getDirection().name().equals("InputOutput")))
				imports.add(com.ctrip.platform.dal.common.enums.ParameterDirection.class.getName());
			Class<?> clazz = field.getJavaClass();
			if (byte[].class.equals(clazz))
				continue;
			if (null == clazz)
				continue;
			if (clazz.getPackage().getName().equals(String.class.getPackage().getName()))
				continue;
			imports.add(clazz.getName());
		}

		return imports;
	}

	public Set<String> getTestImports() {
		Set<String> imports = new TreeSet<String>();
		imports.add(java.util.List.class.getName());
		imports.addAll(this.getPojoImports());
		return imports;
	}

	public Set<String> getPojoImports() {
		Set<String> imports = new TreeSet<String>();

		List<JavaParameterHost> allTypes = new ArrayList<JavaParameterHost>(
				fields);
		for (JavaParameterHost field : allTypes) {
			Class<?> clazz = field.getJavaClass();
			if (byte[].class.equals(clazz))
				continue;
			if (clazz.getPackage().getName().equals(String.class.getPackage().getName()))
				continue;
			imports.add(clazz.getName());
		}
		return imports;
	}

	/**
	 * Get the CTE order by columns to generate row-number
	 * 
	 * @return
	 */
	public String getOverColumns() {
		List<String> tokens = new ArrayList<String>();
		for (JavaParameterHost p : this.fields) {
			if (p.isPrimary())
				tokens.add(p.getName());
		}
		if (tokens.size() > 0)
			return StringUtils.join(tokens, ",");
		else
			return this.fields.get(0).getName();
	}

	public String getPrimaryKeyName() throws Exception{
		if(this.getPrimaryKeys() == null && this.getPrimaryKeys().size() != 1)
			throw new Exception("The multiple primary key is not allowed here!");
		return this.getPrimaryKeys().get(0).getCapitalizedName();
	}
	
	public String getScalarColumn() {
		return (null != this.fields && !this.fields.isEmpty()) ? this.fields
				.get(0).getClassDisplayName() : "";
	}

	public boolean isSampleType() {
		return null != this.fields && this.fields.size() == 1;
	}
}
