package com.ctrip.platform.dal.dao.task;

import java.util.List;

public abstract class AbstractIntArrayBulkTask<T> extends TaskAdapter<T> implements BulkTask<int[], T> {
	@Override
	public int[] merge(List<int[]> results) {
		int[][] counts = results.toArray(new int[results.size()][]);

		int total = 0;
		for(int[] countsInTable: counts)
			total += countsInTable.length;
		
		int[] totalCounts = new int[total];
		int cur = 0;
		for(int[] countsInTable: counts) {
			System.arraycopy(countsInTable, 0, totalCounts, cur, countsInTable.length);
			cur += countsInTable.length;
		}
		
		return totalCounts;
	}
}
