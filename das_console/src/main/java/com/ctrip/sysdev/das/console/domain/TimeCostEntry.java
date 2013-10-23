package com.ctrip.sysdev.das.console.domain;

public class TimeCostEntry {
	private String stage;
	private Integer cost;

	public TimeCostEntry() {
	}
	
	public TimeCostEntry(String segment) {
		String[] pair = segment.split(":");
		stage = pair[0];
		cost = Integer.parseInt(pair[1]);
	}

	public TimeCostEntry(String stage, Integer cost) {
		this.stage = stage;
		this.cost = cost;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public Integer getCost() {
		return cost;
	}

	public void setCost(Integer cost) {
		this.cost = cost;
	}

}
