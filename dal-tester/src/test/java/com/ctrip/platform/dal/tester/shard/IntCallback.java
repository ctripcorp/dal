package com.ctrip.platform.dal.tester.shard;

import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;

public class IntCallback extends DefaultResultCallback {
	public int getInt() {
		waitForDone();
		try {
			if(getResult() instanceof Integer)
				return (Integer)getResult();
			else
				return ((int[])getResult())[0];
		} catch (Throwable e) {
			return 0;
		}
	}
	
	public int[] getIntArray() {
		waitForDone();
		try {
			return (int[])getResult();
		} catch (Throwable e) {
			return null;
		}
	}

	public boolean hasError() {
		return !isSuccess();
	}

}
