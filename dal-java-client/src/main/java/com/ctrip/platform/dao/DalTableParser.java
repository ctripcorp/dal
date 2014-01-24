package com.ctrip.platform.dao;

import java.util.List;

public interface DalTableParser extends ResultSetVisitor {
	List<StatementParameter> getPk(DaoPojo pojo);
	List<StatementParameter> getFields(DaoPojo pojo);
}
