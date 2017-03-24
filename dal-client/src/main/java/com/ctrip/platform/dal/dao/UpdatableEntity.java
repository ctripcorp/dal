package com.ctrip.platform.dal.dao;

import java.util.HashSet;
import java.util.Set;

public class UpdatableEntity implements DalPojo {
	private Set<String> dirtyFlags = new HashSet<>();
	public UpdatableEntity() {
		
	}
	
	public void update(String column) {
		dirtyFlags.add(column);
	}
	
	public void reset() {
		dirtyFlags.clear();
	}
	
	public Set<String> getUpdatedColumns() {
		return dirtyFlags;
	}
}
