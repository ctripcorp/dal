package com.ctrip.platform.dal.dao.configure;

import java.util.Map;

public interface DalComponent {
	void initialize(Map<String, String> settings) throws Exception;
}
