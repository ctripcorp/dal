package com.ctrip.platform.dal.daogen.cs;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.pojo.StoredProcedure;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

public class CSharpSpUpdateHost {
	
	private boolean hasUpdateMethod;

	private List<CSharpParameterHost> updateParameterList;

	private String updateMethodName;

	public boolean isHasUpdateMethod() {
		return hasUpdateMethod;
	}

	public void setHasUpdateMethod(boolean hasUpdateMethod) {
		this.hasUpdateMethod = hasUpdateMethod;
	}

	public List<CSharpParameterHost> getUpdateParameterList() {
		return updateParameterList;
	}

	public void setUpdateParameterList(List<CSharpParameterHost> updateParameterList) {
		this.updateParameterList = updateParameterList;
	}

	public String getUpdateMethodName() {
		return updateMethodName;
	}

	public void setUpdateMethodName(String updateMethodName) {
		this.updateMethodName = updateMethodName;
	}

	
	public static CSharpSpUpdateHost getUpdateSp(int server, String dbName,
			String tableName, List<StoredProcedure> spNames) {

		CSharpSpUpdateHost host = new CSharpSpUpdateHost();
		
		StoredProcedure expectSpa = new StoredProcedure();
		expectSpa.setName(String.format("spA_%s_u", tableName));
		StoredProcedure expectSp3 = new StoredProcedure();
		expectSp3.setName(String.format("sp3_%s_u", tableName));
		StoredProcedure currentSp = null;
		int index = -1;
		
		if( (index = spNames.indexOf(expectSpa)) > 0){
			host.setHasUpdateMethod(true);
			host.setUpdateMethodName(expectSpa.getName());
			currentSp = spNames.get(index);
		}else if((index = spNames.indexOf(expectSp3)) > 0){
			host.setHasUpdateMethod(true);
			host.setUpdateMethodName(expectSp3.getName());
			currentSp = spNames.get(index);
		}else{
			host.setHasUpdateMethod(false);
		}
		
		if(host.isHasUpdateMethod()){
			List<AbstractParameterHost> params =  DbUtils.getSpParams(server, dbName, currentSp, 0);
			List<CSharpParameterHost> realParams = new ArrayList<CSharpParameterHost>();
			for(AbstractParameterHost p : params){
				realParams.add((CSharpParameterHost) p);
			}
			host.setUpdateParameterList(realParams);
		}
		
		return host;

	}
}
