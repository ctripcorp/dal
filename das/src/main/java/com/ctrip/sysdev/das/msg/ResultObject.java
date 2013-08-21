package com.ctrip.sysdev.das.msg;

import java.util.List;

import com.ctrip.sysdev.das.enums.ResultType;

public class ResultObject {
	
	public ResultType resultType;
	
	public int affectRowCount;
	
	public List<AvailableType> resultSet;
	
	public int propertyCount(){
		return 2;
	}

}
