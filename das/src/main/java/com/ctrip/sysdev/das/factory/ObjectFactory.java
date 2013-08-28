package com.ctrip.sysdev.das.factory;

public interface ObjectFactory {

	public <T> T getInstance(Class<T> clazz) throws Exception;

}
