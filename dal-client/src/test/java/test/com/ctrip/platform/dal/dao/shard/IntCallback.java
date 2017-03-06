package test.com.ctrip.platform.dal.dao.shard;

import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;

public class IntCallback extends DefaultResultCallback {
	public int getInt() {
		try {
			waitForDone();
		} catch (InterruptedException e1) {
			throw new RuntimeException(e1);
		}
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
		try {
			waitForDone();
		} catch (InterruptedException e1) {
			throw new RuntimeException(e1);
		}
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
