package com.ctrip.platform.dal.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.mapper.ArrayMapper;

public interface ResultMerger<T> {
	void addPartial(String shard, T partial);
	T merge();
	
	static class ListMerger implements ResultMerger<List<?>>{
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
			if(comparator != null)
				Collections.sort(result, comparator);
			return result;
		}
	}


	static class IntSummary implements ResultMerger<Integer>{
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

	static class LongSummary implements ResultMerger<Long>{
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

	static class DoubleSummary implements ResultMerger<Double>{
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

	static class IntAverage implements ResultMerger<Map<String, Number>>{
		private int count;
		private int sum;
		private String countColumn;
		private String sumColumn;
		private String averageColumn;
		
		public IntAverage() {this("count", "sum", "average");}
		
		public IntAverage(String countColumn, String sumColumn, String averageColumn) {
			this.countColumn = countColumn;
			this.sumColumn = sumColumn;
			this.averageColumn = averageColumn;
		}
		
		@Override
		public void addPartial(String shard, Map<String, Number> partial) {
			count += partial.get(countColumn).intValue();
			sum += partial.get(sumColumn).intValue();
		}

		@Override
		public Map<String, Number> merge() {
			Map<String, Number> result = new HashMap<>();
			result.put(countColumn, count);
			result.put(sumColumn, sum);
			result.put(averageColumn, sum/count);
			
			return result;
		}
	}

	static class LongAverage implements ResultMerger<Map<String, Number>>{
		private int count;
		private long sum;
		private String countColumn;
		private String sumColumn;
		private String averageColumn;
		
		public LongAverage() {this("count", "sum", "average");}
		
		public LongAverage(String countColumn, String sumColumn, String averageColumn) {
			this.countColumn = countColumn;
			this.sumColumn = sumColumn;
			this.averageColumn = averageColumn;
		}
		
		@Override
		public void addPartial(String shard, Map<String, Number> partial) {
			count += partial.get(countColumn).intValue();
			sum += partial.get(sumColumn).longValue();
		}

		@Override
		public Map<String, Number> merge() {
			Map<String, Number> result = new HashMap<>();
			result.put(countColumn, count);
			result.put(sumColumn, sum);
			result.put(averageColumn, sum/count);
			
			return result;
		}
	}

	static class DoubleAverage implements ResultMerger<Map<String, Number>>{
		private int count;
		private double sum;
		private String countColumn;
		private String sumColumn;
		private String averageColumn;
		
		public DoubleAverage() {this("count", "sum", "average");}
		
		public DoubleAverage(String countColumn, String sumColumn, String averageColumn) {
			this.countColumn = countColumn;
			this.sumColumn = sumColumn;
			this.averageColumn = averageColumn;
		}
		
		@Override
		public void addPartial(String shard, Map<String, Number> partial) {
			count += partial.get(countColumn).intValue();
			sum += partial.get(sumColumn).doubleValue();
		}

		@Override
		public Map<String, Number> merge() {
			Map<String, Number> result = new HashMap<>();
			result.put(countColumn, count);
			result.put(sumColumn, sum);
			result.put(averageColumn, sum/count);
			
			return result;
		}
	}

}
