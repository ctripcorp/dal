package com.ctrip.sysdev.das.console.domain;

import java.util.ArrayList;
import java.util.List;

public class TimeCost {
	private String id;
	private List<TimeCostEntry> entries = new ArrayList<TimeCostEntry>();

	public TimeCost() {

	}

	public TimeCost(String id, String values) {
		this.id = id;
		String[] segments = values.split(";");
		for (String segment : segments) {
			entries.add(new TimeCostEntry(segment));
		}
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

	public void merge(TimeCost oldTc) {
		if (oldTc != null)
			entries.addAll(oldTc.entries);
	}
}
