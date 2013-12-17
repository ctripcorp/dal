package com.ctrip.sysdev.das.console.domain;

import java.util.concurrent.atomic.AtomicLong;

public class TimeCostStatistics {
	private AtomicLong totalDecodeCost = new AtomicLong();
	private AtomicLong totalDBCost = new AtomicLong();
	private AtomicLong totalEncodeCost = new AtomicLong();

	public TimeCostStatisticsTO getSnapshot(long totalRequests) {
		return new TimeCostStatisticsTO(
				totalRequests,
				totalDecodeCost.get(), 
				totalDBCost.get(),
				totalEncodeCost.get());
	}

	public void incTotalDecodeCost(long delta) {
		totalDecodeCost.addAndGet(delta);
	}

	public void incTotalDBCost(long delta) {
		totalDBCost.addAndGet(delta);
	}

	public void incTotalEncodeCost(long delta) {
		totalEncodeCost.addAndGet(delta);
	}
}
