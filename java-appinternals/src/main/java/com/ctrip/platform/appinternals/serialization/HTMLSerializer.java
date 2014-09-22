package com.ctrip.platform.appinternals.serialization;

import java.util.Collection;

import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;

public class HTMLSerializer extends Serializer{

	@Override
	public String serializer(ConfigBeanBase bean) throws Exception {
		throw new Exception("Not implement!");
	}

	@Override
	public String serializer(Collection<ConfigBeanBase> beans) throws Exception {
		throw new Exception("Not implement!");
	}

}
