package com.ctrip.platform.dal.exceptions;

public enum ErrorClassify {
	Assert(5000, 5100),
	Validate(5100, 5300),
	Connection(5300, 5400),
	Statement(5400, 5500),
	BuildSQL(5500, 5600),
	Transaction(5600, 5700),
	Extract(5700, 5800),
	LogException(5800, 5900),
	Shard(5900, 6000),
	Unknown(9999, 9999);
	
	private final int min;
	private final int max;
	
	ErrorClassify(int min, int max){
		this.min = min;
		this.max = max;
	}

	public int getMinCode() {
		return this.min;
	}
	
	public int getMaxCode(){
		return this.max;
	}
}