package com.ctrip.platform.dasconsole.domain;

public class Performance {
	private double systemCpuUsage;
	private double processCpuUsage;
	private long freeMemory;
	private long totalMemory;
	private long sysFreeMemory;
	private long sysTotalMemory;
	private long start;
	private long end;

	public long getSysFreeMemory() {
		return sysFreeMemory;
	}

	public void setSysFreeMemory(long sysFreeMemory) {
		this.sysFreeMemory = sysFreeMemory;
	}

	public long getSysTotalMemory() {
		return sysTotalMemory;
	}

	public void setSysTotalMemory(long sysTotalMemory) {
		this.sysTotalMemory = sysTotalMemory;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public double getSystemCpuUsage() {
		return systemCpuUsage;
	}

	public void setSystemCpuUsage(double systemCpuUsage) {
		this.systemCpuUsage = systemCpuUsage;
	}

	public double getProcessCpuUsage() {
		return processCpuUsage;
	}

	public void setProcessCpuUsage(double processCpuUsage) {
		this.processCpuUsage = processCpuUsage;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}
}
