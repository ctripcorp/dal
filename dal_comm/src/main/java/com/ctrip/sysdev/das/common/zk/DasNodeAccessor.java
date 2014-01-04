package com.ctrip.sysdev.das.common.zk;

import java.util.List;

import com.ctrip.sysdev.das.common.zk.to.DasNodeSetting;

public class DasNodeAccessor extends DasZkAccessor {

	public List<String> list() throws Exception {
		return getChildren(NODE);
	}
	
	public DasNodeSetting getDasNodeSetting(String id) throws Exception {
		DasNodeSetting setting = new DasNodeSetting();
		String nodePath = pathOf(NODE, id);
		setting.setDirectory(getStringValue(nodePath, DIRECTORY));
		setting.setMaxHeapSize(getStringValue(nodePath, MAX_HEAP_SIZE));
		setting.setStartingHeapSize(getStringValue(nodePath, STARTING_HEAP_SIZE));
		return setting;
	}
	
	public void createDasNode(String id, DasNodeSetting setting) throws Exception {
		String nodePath = pathOf(NODE, id);
		create(nodePath);
		
		create(nodePath, DIRECTORY, setting.getDirectory());
		create(nodePath, MAX_HEAP_SIZE, setting.getMaxHeapSize());
		create(nodePath, STARTING_HEAP_SIZE, setting.getStartingHeapSize());
	}
	
	 public void removeDasNode(String id) throws Exception {
		// When there is running Das Worker, this operation is denied
		deleteNodeNested(pathOf(NODE, id));
	}
	
	public void updateDasNode(String id, DasNodeSetting setting) throws Exception {
		String nodePath = pathOf(NODE, id);
		setValue(nodePath, DIRECTORY, setting.getDirectory());
		setValue(nodePath, MAX_HEAP_SIZE, setting.getMaxHeapSize());
		setValue(nodePath, STARTING_HEAP_SIZE, setting.getStartingHeapSize());
	}
	
	@Override
	public void initialize() {
		createPath(NODE);
	}
}
