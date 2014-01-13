package com.ctrip.platform.dao;

import java.io.Closeable;
import java.io.IOException;

public interface Recycleable {
	
	public void recycle(Closeable closeable) throws IllegalArgumentException, IOException;

}
