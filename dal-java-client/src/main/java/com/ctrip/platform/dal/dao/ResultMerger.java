package com.ctrip.platform.dal.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public interface ResultMerger<K, T> {
	void addPartial(String shard, T partial);
	K merge();
	
	static class ListMerger implements ResultMerger<List<?>, List<?>>{
		private List<Object> result = new ArrayList<>();
		private Comparator<Object> comparator;
		
		public ListMerger() {
			this(null);
		}
		
		public ListMerger(Comparator<Object> comparator) {
			this.comparator = comparator;
		}
		
		
		@Override
		public void addPartial(String shard, List<?> partial) {
			result.addAll(partial);
		}

		@Override
		public List<?> merge() {
			Collections.sort(result, comparator);
			return result;
		}
	}


	static class IntSummary implements ResultMerger<Integer, Integer>{
		private int total;
		@Override
		public void addPartial(String shard, Integer partial) {
			total += partial.intValue();
		}

		@Override
		public Integer merge() {
			return total;
		}
	}

	static class LongSummary implements ResultMerger<Long, Long>{
		private long total;
		@Override
		public void addPartial(String shard, Long partial) {
			total += partial.longValue();
		}

		@Override
		public Long merge() {
			return total;
		}
	}

	static class DoubleSummary implements ResultMerger<Double, Double>{
		private double total;
		@Override
		public void addPartial(String shard, Double partial) {
			total += partial.doubleValue();
		}

		@Override
		public Double merge() {
			return total;
		}
	}
//
//	static class IntAverage implements ResultMerger<Integer, Integer>{
//		private int total;
//		@Override
//		public void addPartial(String shard, Integer partial) {
//			total += partial.intValue();
//		}
//
//		@Override
//		public Integer merge() {
//			return total;
//		}
//	}
//
}
