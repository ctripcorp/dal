package com.ctrip.sysdev.msg;

import java.util.List;

import com.ctrip.sysdev.enums.ResultType;

public class ResultObject {
	
	public ResultType resultType;
	
	public int affectRowCount;
	
	public List<AvailableType> resultSet;

}
