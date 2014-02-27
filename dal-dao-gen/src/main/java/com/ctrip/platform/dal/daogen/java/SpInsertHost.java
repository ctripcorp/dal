package com.ctrip.platform.dal.daogen.java;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.pojo.StoredProcedure;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

public class SpInsertHost {

	private boolean hasInsertMethod;

	private List<JavaParameterHost> insertParameterList;

	private String insertMethodName;

	public boolean isHasInsertMethod() {
		return hasInsertMethod;
	}

	public void setHasInsertMethod(boolean hasInsertMethod) {
		this.hasInsertMethod = hasInsertMethod;
	}

	public List<JavaParameterHost> getInsertParameterList() {
		return insertParameterList;
	}

	public void setInsertParameterList(
			List<JavaParameterHost> insertParameterList) {
		this.insertParameterList = insertParameterList;
	}

	public String getInsertMethodName() {
		return insertMethodName;
	}

	public void setInsertMethodName(String insertMethodName) {
		this.insertMethodName = insertMethodName;
	}

	public static SpInsertHost getInsertSp(int server, String dbName,
			String tableName, List<StoredProcedure> spNames) {

		SpInsertHost host = new SpInsertHost();
		
		StoredProcedure expectSpa = new StoredProcedure();
		expectSpa.setName(String.format("spA_%s_i", tableName));
		StoredProcedure expectSp3 = new StoredProcedure();
		expectSp3.setName(String.format("sp3_%s_i", tableName));
		StoredProcedure currentSp = null;
		int index = -1;
		
		if( (index = spNames.indexOf(expectSpa)) > 0){
			host.setHasInsertMethod(true);
			host.setInsertMethodName(expectSpa.getName());
			currentSp = spNames.get(index);
		}else if((index = spNames.indexOf(expectSp3)) > 0){
			host.setHasInsertMethod(true);
			host.setInsertMethodName(expectSp3.getName());
			currentSp = spNames.get(index);
		}else{
			host.setHasInsertMethod(false);
		}
		
		if(host.isHasInsertMethod()){
			List<AbstractParameterHost> params =  DbUtils.getSpParams(server, dbName, currentSp, 0);
			List<JavaParameterHost> realParams = new ArrayList<JavaParameterHost>();
			for(AbstractParameterHost p : params){
				realParams.add((JavaParameterHost) p);
			}
			host.setInsertParameterList(realParams);
		}
		
		return host;

	}


}
