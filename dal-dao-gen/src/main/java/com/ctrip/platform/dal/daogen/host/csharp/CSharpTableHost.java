package com.ctrip.platform.dal.daogen.host.csharp;

import java.util.Collections;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class CSharpTableHost {

	private String nameSpace;
	private DatabaseCategory databaseCategory;
	private String dbSetName;
	private String tableName;
	private String className;
	private boolean isTable;

	//spa/sp3 or cud by sql
	private boolean isSpa;
	private CSharpSpaOperationHost spaInsert;
	private CSharpSpaOperationHost spaUpdate;
	private CSharpSpaOperationHost spaDelete;
	private List<CSharpMethodHost> extraMethods;
	
	private List<CSharpParameterHost> primaryKeys;

	private List<CSharpParameterHost> columns;
	
	private boolean hasPagination;
	private boolean hasSpt;
	private boolean hasSptI;
	private boolean hasSptD;
	private boolean hasSptU;
	
	private String spName;
	
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

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace= nameSpace;
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

	public CSharpSpaOperationHost getSpaInsert() {
		return spaInsert;
	}

	public void setSpaInsert(CSharpSpaOperationHost spaInsert) {
		this.spaInsert = spaInsert;
	}

	public CSharpSpaOperationHost getSpaUpdate() {
		return spaUpdate;
	}

	public void setSpaUpdate(CSharpSpaOperationHost spaUpdate) {
		this.spaUpdate = spaUpdate;
	}

	public CSharpSpaOperationHost getSpaDelete() {
		return spaDelete;
	}

	public void setSpaDelete(CSharpSpaOperationHost spaDelete) {
		this.spaDelete = spaDelete;
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
		Collections.sort(this.columns);
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
