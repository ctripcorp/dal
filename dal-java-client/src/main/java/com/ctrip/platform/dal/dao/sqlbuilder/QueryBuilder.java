package com.ctrip.platform.dal.dao.sqlbuilder;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;


public interface QueryBuilder extends SqlBuilder {
	QueryBuilder requireFirst();
	
	QueryBuilder requireSingle();
	
	QueryBuilder nullable();
	
	boolean isRequireFirst();

	boolean isRequireSingle();

	boolean isNullable();

	<T> ResultMerger<T> getResultMerger(DalHints hints);

	<T> QueryBuilder mapWith(DalRowMapper<T> mapper);

	<T> DalResultSetExtractor<T> getResultExtractor(DalHints hints);
}
