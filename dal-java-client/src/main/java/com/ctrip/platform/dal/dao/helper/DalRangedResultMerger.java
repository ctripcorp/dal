package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ctrip.platform.dal.dao.ResultMerger;

public class DalRangedResultMerger<T> implements ResultMerger<List<T>>{
	private Comparator<T> comparator;
	private int start;
	private int count;
	private List<T> results = new ArrayList<>();
	
	// Select top
	public DalRangedResultMerger(int count) {
		this(null, count);
	}
	
	// Select top
	public DalRangedResultMerger(Comparator<T> comparator, int count) {
		this(comparator, 0, count);
	}
	
	public DalRangedResultMerger(int start, int count) {
		this(null, start, count);
	}

	// Select from to
	public DalRangedResultMerger(Comparator<T> comparator, int start, int count) {
		if(start < 0)
			throw new IllegalArgumentException("Start can not be negative number.");
		if(count < 0)
			throw new IllegalArgumentException("Count can not be negative number.");
		
		this.comparator = comparator;
		this.start = start;
		this.count = count;
	}
	
	@Override
	public void addPartial(String shard, List<T> partial) throws SQLException {
		if(partial!=null)
			results.addAll(partial);
	}

	@Override
	public List<T> merge() throws SQLException {
		if(comparator != null)
			Collections.sort(results, comparator);

		if(start >= results.size())
			return new ArrayList<>();
			
		return start + count > results.size() ? results.subList(start, results.size()) : results.subList(start, start + count);
	}
}
