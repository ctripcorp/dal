package com.ctrip.sysdev.das.console.domain;

public class TimeCostStatisticsTO {
	private long totalRequests;
	private long totalDecodeCost;
	private long totalDBCost;
	private long totalEncodeCost;

	private long averageDecodeCost;
	private long averageDBCost;
	private long averageEncodeCost;
	
	public TimeCostStatisticsTO (
			long totalRequests,
			long totalDecodeCost,
			long totalDBCost,
			long totalEncodeCost) {
		this.totalRequests = totalRequests;
		this.totalDecodeCost = totalDecodeCost;
		this.totalDBCost = totalDBCost;
		this.totalEncodeCost = totalEncodeCost;
		
		averageDecodeCost = totalDecodeCost / totalRequests;
		averageDBCost = totalDBCost / totalRequests;
		averageEncodeCost = totalEncodeCost / totalRequests;
	}
	
	public long getTotalRequests() {
		return totalRequests;
	}
	public void setTotalRequests(long totalRequests) {
		this.totalRequests = totalRequests;
	}
	public long getTotalDecodeCost() {
		return totalDecodeCost;
	}
	public void setTotalDecodeCost(long totalDecodeCost) {
		this.totalDecodeCost = totalDecodeCost;
	}
	public long getTotalDBCost() {
		return totalDBCost;
	}
	public void setTotalDBCost(long totalDBCost) {
		this.totalDBCost = totalDBCost;
	}
	public long getTotalEncodeCost() {
		return totalEncodeCost;
	}
	public void setTotalEncodeCost(long totalEncodeCost) {
		this.totalEncodeCost = totalEncodeCost;
	}
	public long getAverageDecodeCost() {
		return averageDecodeCost;
	}
	public void setAverageDecodeCost(long averageDecodeCost) {
		this.averageDecodeCost = averageDecodeCost;
	}
	public long getAverageDBCost() {
		return averageDBCost;
	}
	public void setAverageDBCost(long averageDBCost) {
		this.averageDBCost = averageDBCost;
	}
	public long getAverageEncodeCost() {
		return averageEncodeCost;
	}
	public void setAverageEncodeCost(long averageEncodeCost) {
		this.averageEncodeCost = averageEncodeCost;
	}
}
