package com.ctrip.platform.dal.dao.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ctrip.platform.dal.dao.ResultMerger;

public class DalFirstResultMerger<T> implements ResultMerger<T>{
	private List<T> result = new ArrayList<>();
	private Comparator<T> comparator;
	
	public DalFirstResultMerger() {
		this(null);
	}
	public DalFirstResultMerger(Comparator<T> comparator) {
		this.comparator = comparator;
	}
	
	@Override
	public void addPartial(String shard, T partial) {
		if(partial!=null)
			result.add(partial);
	}

	@Override
	public T merge() {
		if(comparator != null)
			Collections.sort(result, comparator);
		
		if(result.size() > 0)
			return result.get(0);
		else
			return null;
	}
}
