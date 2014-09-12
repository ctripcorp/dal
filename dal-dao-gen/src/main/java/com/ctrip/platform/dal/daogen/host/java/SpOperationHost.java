package com.ctrip.platform.dal.daogen.host.java;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
//http://conf.ctripcorp.com/pages/viewpage.action?pageId=54479645
public class SpOperationHost {
	private boolean exist;
	private List<JavaParameterHost> parameters = new ArrayList<JavaParameterHost>();
	//Sp Name
	private String methodName;
	//Sp Name
	private String spName="";
	//Sp Name
	private String batchSpName="";
	
	private String type;

	public static SpOperationHost getSpaOperation(String dbName,
			String tableName, List<StoredProcedure> spNames, String operation) {

		SpOperationHost host = new SpOperationHost();
		
		StoredProcedure expectSpa = new StoredProcedure();
		expectSpa.setName(String.format("spA_%s_%s", tableName, operation));
		StoredProcedure expectSp3 = new StoredProcedure();
		expectSp3.setName(String.format("sp3_%s_%s", tableName, operation));
		StoredProcedure currentSp = null;
		int index = -1;
		
		if( (index = spNames.indexOf(expectSpa)) > 0){
			host.exist = true;
			host.methodName = expectSpa.getName();
			host.setType("spA");
			currentSp = spNames.get(index);
		}else if((index = spNames.indexOf(expectSp3)) > 0){
			host.exist = true;
			host.methodName = expectSp3.getName();
			host.setType("sp3");
			currentSp = spNames.get(index);
		}else{
			host.exist = false;
		}
		
		if(host.exist){
			List<AbstractParameterHost> params =  DbUtils.getSpParams(dbName, currentSp, CurrentLanguage.Java);
			List<JavaParameterHost> realParams = new ArrayList<JavaParameterHost>();
			for(AbstractParameterHost p : params){
				realParams.add((JavaParameterHost) p);
			}
			host.parameters = realParams;
		}
		
		return host;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public List<JavaParameterHost> getParameters() {
		return parameters;
	}

	public void setParameters(List<JavaParameterHost> parameters) {
		this.parameters = parameters;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}

	public String getBatchSpName() {
		return batchSpName;
	}

	public void setBatchSpName(String batchSpName) {
		this.batchSpName = batchSpName;
	}

}