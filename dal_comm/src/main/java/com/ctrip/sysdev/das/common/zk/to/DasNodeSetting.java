package com.ctrip.sysdev.das.common.zk.to;

public class DasNodeSetting {
	private String maxHeapSize;
	private String startingHeapSize;
	private String directory;

	public String getMaxHeapSize() {
		return maxHeapSize;
	}

	public void setMaxHeapSize(String maxHeapSize) {
		this.maxHeapSize = maxHeapSize;
	}

	public String getStartingHeapSize() {
		return startingHeapSize;
	}

	public void setStartingHeapSize(String startingHeapSize) {
		this.startingHeapSize = startingHeapSize;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}
}
