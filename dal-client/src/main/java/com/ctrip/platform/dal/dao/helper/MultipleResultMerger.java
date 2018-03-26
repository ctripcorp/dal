package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.dao.ResultMerger;

public class MultipleResultMerger implements ResultMerger<List<?>> {
	private List<ResultMerger<?>> mergers = new ArrayList<>();
	
	public <T> void add(ResultMerger<T> merger) {
		mergers.add(merger);
	}
	
	@Override
	public void addPartial(String shard, List<?> partial) throws SQLException {
		for(int i = 0; i < partial.size(); i++) {
			ResultMerger merger = mergers.get(i);
			merger.addPartial(shard, partial.get(i));
		}
	}

	@Override
	public List merge() throws SQLException {
		List result = new ArrayList<>();
		for(ResultMerger merger: mergers)
			result.add(merger.merge());
		return result;
	}

}
