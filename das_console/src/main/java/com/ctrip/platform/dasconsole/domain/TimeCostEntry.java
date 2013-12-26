package com.ctrip.platform.dasconsole.domain;

public class TimeCostEntry {
	private String stage;
	private Long cost;

	public TimeCostEntry() {
	}
	
	public TimeCostEntry(String stage, Long cost) {
		this.stage = stage;
		this.cost = cost;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public Long getCost() {
		return cost;
	}

	public void setCost(Long cost) {
		this.cost = cost;
	}
}
