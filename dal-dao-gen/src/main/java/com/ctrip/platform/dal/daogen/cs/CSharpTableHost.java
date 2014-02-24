package com.ctrip.platform.dal.daogen.cs;

import java.util.List;

import com.ctrip.platform.dal.daogen.pojo.DatabaseCategory;

public class CSharpTableHost {

	private String nameSpaceEntity;

	private String nameSpaceIDao;

	private String nameSpaceDao;
	
	private DatabaseCategory databaseCategory;

	private String dbSetName;

	private String tableName;
	
	private String className;

	private boolean isTable;

	private boolean isSpa;

	private boolean hasInsertMethod;

	private List<CSharpParameterHost> insertParameterList;

	private String insertMethodName;

	private boolean hasUpdateMethod;

	private List<CSharpParameterHost> updateParameterList;

	private String updateMethodName;

	private boolean hasDeleteMethod;

	private List<CSharpParameterHost> deleteParameterList;

	private String deleteMethodName;

	private List<CSharpParameterHost> primaryKeys;

	private List<CSharpParameterHost> columns;
	
	private boolean hasPagination;

	private boolean hasSpt;

	private boolean hasSptI;

	private boolean hasSptD;

	private boolean hasSptU;
	
	private String spName;
	
	private List<CSharpMethodHost> extraMethods;
	
	public List<CSharpMethodHost> getExtraMethods() {
		return extraMethods;
	}

	public void setExtraMethods(List<CSharpMethodHost> extraMethods) {
		this.extraMethods = extraMethods;
	}

	private List<CSharpParameterHost> spParams;

	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}

	public List<CSharpParameterHost> getSpParams() {
		return spParams;
	}

	public void setSpParams(List<CSharpParameterHost> spParams) {
		this.spParams = spParams;
	}

	public String getNameSpaceEntity() {
		return nameSpaceEntity;
	}

	public void setNameSpaceEntity(String nameSpaceEntity) {
		this.nameSpaceEntity = nameSpaceEntity;
	}

	public String getNameSpaceIDao() {
		return nameSpaceIDao;
	}

	public void setNameSpaceIDao(String nameSpaceIDao) {
		this.nameSpaceIDao = nameSpaceIDao;
	}

	public String getNameSpaceDao() {
		return nameSpaceDao;
	}

	public void setNameSpaceDao(String nameSpaceDao) {
		this.nameSpaceDao = nameSpaceDao;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public DatabaseCategory getDatabaseCategory() {
		return databaseCategory;
	}

	public void setDatabaseCategory(DatabaseCategory databaseCategory) {
		this.databaseCategory = databaseCategory;
	}

	public String getDbSetName() {
		return dbSetName;
	}

	public void setDbSetName(String dbSetName) {
		this.dbSetName = dbSetName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public boolean isTable() {
		return isTable;
	}

	public void setTable(boolean isTable) {
		this.isTable = isTable;
	}

	public boolean isSpa() {
		return isSpa;
	}

	public void setSpa(boolean isSpa) {
		this.isSpa = isSpa;
	}

	public boolean isHasInsertMethod() {
		return hasInsertMethod;
	}

	public void setHasInsertMethod(boolean hasInsertMethod) {
		this.hasInsertMethod = hasInsertMethod;
	}

	public List<CSharpParameterHost> getInsertParameterList() {
		return insertParameterList;
	}

	public void setInsertParameterList(
			List<CSharpParameterHost> insertParameterList) {
		this.insertParameterList = insertParameterList;
	}

	public String getInsertMethodName() {
		return insertMethodName;
	}

	public void setInsertMethodName(String insertMethodName) {
		this.insertMethodName = insertMethodName;
	}

	public boolean isHasUpdateMethod() {
		return hasUpdateMethod;
	}

	public void setHasUpdateMethod(boolean hasUpdateMethod) {
		this.hasUpdateMethod = hasUpdateMethod;
	}

	public List<CSharpParameterHost> getUpdateParameterList() {
		return updateParameterList;
	}

	public void setUpdateParameterList(
			List<CSharpParameterHost> updateParameterList) {
		this.updateParameterList = updateParameterList;
	}

	public String getUpdateMethodName() {
		return updateMethodName;
	}

	public void setUpdateMethodName(String updateMethodName) {
		this.updateMethodName = updateMethodName;
	}

	public boolean isHasDeleteMethod() {
		return hasDeleteMethod;
	}

	public void setHasDeleteMethod(boolean hasDeleteMethod) {
		this.hasDeleteMethod = hasDeleteMethod;
	}

	public List<CSharpParameterHost> getDeleteParameterList() {
		return deleteParameterList;
	}

	public void setDeleteParameterList(
			List<CSharpParameterHost> deleteParameterList) {
		this.deleteParameterList = deleteParameterList;
	}

	public String getDeleteMethodName() {
		return deleteMethodName;
	}

	public void setDeleteMethodName(String deleteMethodName) {
		this.deleteMethodName = deleteMethodName;
	}

	public List<CSharpParameterHost> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(List<CSharpParameterHost> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public List<CSharpParameterHost> getColumns() {
		return columns;
	}

	public void setColumns(List<CSharpParameterHost> columns) {
		this.columns = columns;
	}
	
	public boolean isHasPagination() {
		return hasPagination;
	}

	public void setHasPagination(boolean hasPagination) {
		this.hasPagination = hasPagination;
	}

	public boolean isHasSpt() {
		return hasSpt;
	}

	public void setHasSpt(boolean hasSpt) {
		this.hasSpt = hasSpt;
	}

	public boolean isHasSptI() {
		return hasSptI;
	}

	public void setHasSptI(boolean hasSptI) {
		this.hasSptI = hasSptI;
	}

	public boolean isHasSptD() {
		return hasSptD;
	}

	public void setHasSptD(boolean hasSptD) {
		this.hasSptD = hasSptD;
	}

	public boolean isHasSptU() {
		return hasSptU;
	}

	public void setHasSptU(boolean hasSptU) {
		this.hasSptU = hasSptU;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CSharpTableHost) {
			CSharpTableHost castedObj = (CSharpTableHost) obj;
			return castedObj.getDbSetName().equals(this.getDbSetName())
					&& castedObj.getTableName().equals(this.getTableName());
		}

		return false;
	}

}
