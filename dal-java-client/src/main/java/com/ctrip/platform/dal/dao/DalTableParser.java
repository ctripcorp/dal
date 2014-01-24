package com.ctrip.platform.dal.dao;

import java.util.List;

public interface DalTableParser extends ResultSetVisitor {
	List<StatementParameter> getPk(DaoPojo pojo);
	List<StatementParameter> getFields(DaoPojo pojo);
}
