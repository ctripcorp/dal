package com.ctrip.sysdev.das.msg;

import com.ctrip.sysdev.das.enums.ResultType;

public class ResponseObject {

	public ResultType resultType;

	public int affectRowCount;

	public int chunkCount;

	public int recordPerChunk;

	public int propertyCount() {

		if (resultType == ResultType.CUD) {
			return 2;
		} else {
			return 3;
		}
	}

}
