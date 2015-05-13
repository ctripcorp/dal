package com.ctrip.platform.dal.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public interface ResultMerger<T> {
	void addPartial(String shard, T partial) throws SQLException;
	T merge() throws SQLException;
	
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

	static class BigIntegerSummary implements ResultMerger<BigInteger>{
		private BigInteger total;
		@Override
		public void addPartial(String shard, BigInteger partial) {
			if(total == null)
				total = partial;
			else
				total.add(partial);
		}

		@Override
		public BigInteger merge() {
			return total;
		}
	}

	static class BigDecimalSummary implements ResultMerger<BigDecimal>{
		private BigDecimal total;
		@Override
		public void addPartial(String shard, BigDecimal partial) {
			if(total == null)
				total = partial;
			else
				total.add(partial);
		}

		@Override
		public BigDecimal merge() {
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

	static class BigIntegerAverage implements ResultMerger<Map<String, Number>>{
		private int count;
		private BigInteger sum;
		private String countColumn;
		private String sumColumn;
		private String averageColumn;
		
		public BigIntegerAverage() {this("count", "sum", "average");}
		
		public BigIntegerAverage(String countColumn, String sumColumn, String averageColumn) {
			this.countColumn = countColumn;
			this.sumColumn = sumColumn;
			this.averageColumn = averageColumn;
		}
		
		@Override
		public void addPartial(String shard, Map<String, Number> partial) {
			count += partial.get(countColumn).intValue();
			if(sum == null)
				sum = (BigInteger)partial.get(sumColumn);
			else
				sum.add((BigInteger)partial.get(sumColumn));
		}

		@Override
		public Map<String, Number> merge() {
			Map<String, Number> result = new HashMap<>();
			result.put(countColumn, count);
			result.put(sumColumn, sum);
			result.put(averageColumn, sum.divide(new BigInteger(String.valueOf(count))));
			
			return result;
		}
	}

	static class BigDecimalAverage implements ResultMerger<Map<String, Number>>{
		private int count;
		private BigDecimal sum;
		private String countColumn;
		private String sumColumn;
		private String averageColumn;
		
		public BigDecimalAverage() {this("count", "sum", "average");}
		
		public BigDecimalAverage(String countColumn, String sumColumn, String averageColumn) {
			this.countColumn = countColumn;
			this.sumColumn = sumColumn;
			this.averageColumn = averageColumn;
		}
		
		@Override
		public void addPartial(String shard, Map<String, Number> partial) {
			count += partial.get(countColumn).intValue();
			if(sum == null)
				sum = (BigDecimal)partial.get(sumColumn);
			else
				sum.add((BigDecimal)partial.get(sumColumn));
		}

		@Override
		public Map<String, Number> merge() {
			Map<String, Number> result = new HashMap<>();
			result.put(countColumn, count);
			result.put(sumColumn, sum);
			result.put(averageColumn, sum.divide(new BigDecimal(String.valueOf(count))));
			
			return result;
		}
	}
}
