package com.ctrip.platform.dao.client;

import java.util.List;

import com.ctrip.platform.dao.param.StatementParameter;

public interface PojoParser {
	List<StatementParameter> getPk(DaoPojo pojo);
	List<StatementParameter> getFields(DaoPojo pojo);
}
