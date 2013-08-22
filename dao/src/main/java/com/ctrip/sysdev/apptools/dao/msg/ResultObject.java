package com.ctrip.sysdev.apptools.dao.msg;

import java.util.List;

import com.ctrip.sysdev.apptools.dao.enums.ResultType;

public class ResultObject {
	
	public ResultType resultType;
	
	public int affectRowCount;
	
	public List<AvailableType> resultSet;

}
