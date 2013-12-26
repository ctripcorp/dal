package com.ctrip.platform.dasconsole.domain;

import java.util.ArrayList;
import java.util.List;

public class TimeCost {
	private String id;
	private List<TimeCostEntry> entries = new ArrayList<TimeCostEntry>();

	public TimeCost() {

	}

	public TimeCost(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<TimeCostEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<TimeCostEntry> entries) {
		this.entries = entries;
	}

	public void add(TimeCostEntry entry) {
		entries.add(entry);
	}
}
