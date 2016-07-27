package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalSingleResultExtractor<T> implements DalResultSetExtractor<T> {
	private DalRowMapper<T> mapper;
	private boolean requireSingle;
	
	public DalSingleResultExtractor(DalRowMapper<T> mapper, boolean requireSingle) {
		this.mapper = mapper;
		this.requireSingle = requireSingle;
	}

	@Override
	public T extract(ResultSet rs) throws SQLException {
		T result = null;
		if(rs.next()) {
			result = mapper.map(rs, 0);
			if(rs.next() && requireSingle)
				throw new DalException(ErrorCode.AssertSingle);
		}
		return result;
	}
}
