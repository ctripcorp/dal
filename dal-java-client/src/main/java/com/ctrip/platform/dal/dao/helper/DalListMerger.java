package com.ctrip.platform.dal.dao.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ctrip.platform.dal.dao.ResultMerger;

public class DalListMerger<T> implements ResultMerger<List<T>> {
	private List<T> result = new ArrayList<>();
	private Comparator<T> comparator;
	
	public DalListMerger() {
		this(null);
	}
	
	public DalListMerger(Comparator<T> comparator) {
		this.comparator = comparator;
	}
	
	
	@Override
	public void addPartial(String shard, List<T> partial) {
		if(partial!=null)
			result.addAll(partial);
	}

	@Override
	public List<T> merge() {
		if(comparator != null)
			Collections.sort(result, comparator);
		return result;
	}
}