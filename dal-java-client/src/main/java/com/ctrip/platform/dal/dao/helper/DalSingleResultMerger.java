package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalSingleResultMerger<T> implements ResultMerger<T>{
	private T result;
	
	@Override
	public void addPartial(String shard, T partial) throws SQLException {
		if(partial == null)
			return;
		
		if(result == null)
			result = partial;
		else
			throw new DalException(ErrorCode.AssertSingle);
	}

	@Override
	public T merge() {
		return result;
	}
}
