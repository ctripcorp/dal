package com.ctrip.platform.dal.daogen.cs;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.pojo.StoredProcedure;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

public class CSharpSpDeleteHost {
	
	private boolean hasDeleteMethod;

	private List<CSharpParameterHost> deleteParameterList;

	private String deleteMethodName;

	public boolean isHasDeleteMethod() {
		return hasDeleteMethod;
	}

	public void setHasDeleteMethod(boolean hasDeleteMethod) {
		this.hasDeleteMethod = hasDeleteMethod;
	}

	public List<CSharpParameterHost> getDeleteParameterList() {
		return deleteParameterList;
	}

	public void setDeleteParameterList(List<CSharpParameterHost> deleteParameterList) {
		this.deleteParameterList = deleteParameterList;
	}

	public String getDeleteMethodName() {
		return deleteMethodName;
	}

	public void setDeleteMethodName(String deleteMethodName) {
		this.deleteMethodName = deleteMethodName;
	}
	
	public static CSharpSpDeleteHost getDeleteSp(int server, String dbName,
			String tableName, List<StoredProcedure> spNames) {

		CSharpSpDeleteHost host = new CSharpSpDeleteHost();
		
		StoredProcedure expectSpa = new StoredProcedure();
		expectSpa.setName(String.format("spA_%s_d", tableName));
		StoredProcedure expectSp3 = new StoredProcedure();
		expectSp3.setName(String.format("sp3_%s_d", tableName));
		StoredProcedure currentSp = null;
		int index = -1;
		
		if( (index = spNames.indexOf(expectSpa)) > 0){
			host.setHasDeleteMethod(true);
			host.setDeleteMethodName(expectSpa.getName());
			currentSp = spNames.get(index);
		}else if((index = spNames.indexOf(expectSp3)) > 0){
			host.setHasDeleteMethod(true);
			host.setDeleteMethodName(expectSp3.getName());
			currentSp = spNames.get(index);
		}else{
			host.setHasDeleteMethod(false);
		}
		
		if(host.isHasDeleteMethod()){
			List<AbstractParameterHost> params =  DbUtils.getSpParams(server, dbName, currentSp, 0);
			List<CSharpParameterHost> realParams = new ArrayList<CSharpParameterHost>();
			for(AbstractParameterHost p : params){
				realParams.add((CSharpParameterHost) p);
			}
			host.setDeleteParameterList(realParams);
		}
		
		return host;

	}

}
