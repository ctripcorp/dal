package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;


public interface SelectBuilder extends SqlBuilder {
	SelectBuilder requireFirst();
	
	SelectBuilder requireSingle();
	
	SelectBuilder nullable();
	
	SelectBuilder simpleType();
	
	SelectBuilder range(int start, int count);
	
	boolean isRequireFirst();

	boolean isRequireSingle();

	boolean isNullable();
	
	<T> SelectBuilder mergerWith(ResultMerger<T> merger);
	
	<T> ResultMerger<T> getResultMerger(DalHints hints);

	<T> SelectBuilder mapWith(DalRowMapper<T> mapper);
	
	<T> SelectBuilder mapWith(Class<T> type);
	
	<T> SelectBuilder extractorWith(DalResultSetExtractor<T> extractor);
	
	<T> DalResultSetExtractor<T> getResultExtractor(DalHints hints) throws SQLException;
}
