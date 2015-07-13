package com.ctrip.platform.dal.tester.shard;

import java.util.concurrent.atomic.AtomicReference;

import com.ctrip.platform.dal.dao.DalResultCallback;

public class IntCallback implements DalResultCallback {
	AtomicReference<Object> result = new AtomicReference<>();
	@Override
	public <T> void onResult(T result) {
		this.result.set(result);;
	}
	
	public int getInt() {
		while(result.get() == null)
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
			}
		try {
			return ((int[])result.get())[0];
		} catch (Throwable e) {
			return 0;
		}
	}
	
	public int[] getIntArray() {
		while(result.get() == null)
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
			}
		try {
			return (int[])result.get();
		} catch (Throwable e) {
			return null;
		}
	}

}
