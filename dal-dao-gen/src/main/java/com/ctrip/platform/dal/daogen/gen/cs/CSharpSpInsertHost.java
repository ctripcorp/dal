package com.ctrip.platform.dal.daogen.gen.cs;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.daogen.gen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.pojo.StoredProcedure;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

public class CSharpSpInsertHost {

	private boolean hasInsertMethod;

	private List<CSharpParameterHost> insertParameterList;

	private String insertMethodName;

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

	public static CSharpSpInsertHost getInsertSp(int server, String dbName,
			String tableName, List<StoredProcedure> spNames) {

		CSharpSpInsertHost host = new CSharpSpInsertHost();
		
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
			List<CSharpParameterHost> realParams = new ArrayList<CSharpParameterHost>();
			for(AbstractParameterHost p : params){
				realParams.add((CSharpParameterHost) p);
			}
			host.setInsertParameterList(realParams);
		}
		
		return host;

	}

}
