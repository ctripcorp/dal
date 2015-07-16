package com.ctrip.platform.dal.dao.helper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.ctrip.platform.dal.dao.DalResultCallback;

public class DefaultResultCallback implements DalResultCallback {
	private AtomicBoolean done = new AtomicBoolean(false);
	private AtomicReference<Object> result = new AtomicReference<>();
	private AtomicReference<Throwable> e = new AtomicReference<>();
	
	@Override
	public <T> void onResult(T result) {
		this.result.set(result);
		done.set(true);
	}
	
	@Override
	public void onError(Throwable e) {
		this.e.set(e);
		done.set(true);
	}
	
	public void waitForDone() {
		while(done.get() == false)
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
	}

	public void waitForDone(int timeout) {
		int i = 0;
		while(done.get() == false && i < timeout) {
			try {
				i++;
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}

	public boolean isDone() {
		return done.get();
	}

	public boolean isSuccess() {
		return e.get() == null;
	}
	
	public Object getResult() {
		return result.get();
	}

	public Throwable getError() {
		return e.get();
	}
	
	public void reset() {
		done.set(false);
		e.set(null);
		result.set(null);
	}
}
