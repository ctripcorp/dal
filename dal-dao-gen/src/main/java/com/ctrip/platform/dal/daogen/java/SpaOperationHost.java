package com.ctrip.platform.dal.daogen.java;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

public class SpaOperationHost {
	private boolean exist;
	private List<JavaParameterHost> parameters = new ArrayList<JavaParameterHost>();
	private String methodName;
	
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

	public static SpaOperationHost getSpaOperation(String dbName,
			String tableName, List<StoredProcedure> spNames, String operation) {

		SpaOperationHost host = new SpaOperationHost();
		
		StoredProcedure expectSpa = new StoredProcedure();
		expectSpa.setName(String.format("spA_%s_%s", tableName, operation));
		StoredProcedure expectSp3 = new StoredProcedure();
		expectSp3.setName(String.format("sp3_%s_%s", tableName, operation));
		StoredProcedure currentSp = null;
		int index = -1;
		
		if( (index = spNames.indexOf(expectSpa)) > 0){
			host.exist = true;
			host.methodName = expectSpa.getName();
			currentSp = spNames.get(index);
		}else if((index = spNames.indexOf(expectSp3)) > 0){
			host.exist = true;
			host.methodName = expectSp3.getName();
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
}
