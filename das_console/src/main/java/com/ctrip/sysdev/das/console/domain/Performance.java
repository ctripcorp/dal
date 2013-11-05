package com.ctrip.sysdev.das.console.domain;

public class Performance {
	private String ip;
	private String id;
	private double systemCpuUsage;
	private double processCpuUsage;
	private long freeMemory;
	private long totalMemory;
	private long start;
	private long end;

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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
