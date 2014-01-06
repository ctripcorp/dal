package com.ctrip.sysdev.das.common.zk;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.ZooKeeper;

import com.ctrip.sysdev.das.common.zk.to.DasWorker;

public class DasWorkerAccessor extends DasZkAccessor {

	public DasWorkerAccessor(ZooKeeper zk) {
		super(zk);
	}
	
	public List<DasWorker> list() throws Exception {
		return convertToWorker(getChildren(WORKER));
	}
	
	public List<DasWorker> listByLogicDB(String logicDbName) throws Exception {
		return convertToWorker(getChildren(DB_NODE, logicDbName));
	}
	
	public List<DasWorker> listByLogicDBGroup(String logicDbGroupName) throws Exception {
		return convertToWorker(getChildren(DB_GROUP_NODE, logicDbGroupName));
	}
	
	public boolean isRegistered(String id, int port) throws Exception {
		return exists(pathOf(WORKER, String.valueOf(id)), String.valueOf(port));
	}
	
	public void register(String id, int port) throws Exception {
		this.register(pathOf(WORKER, String.valueOf(id)));
	}
	
	public void registerByLogicDB(String id, int port, String logicDb) throws Exception {
		this.register(pathOf(pathOf(DB_NODE, logicDb), getId(id, port)));
	}
	
	public void registerByLogicDbGroup(String id, int port, String logicDbGroup) throws Exception {
		this.register(pathOf(pathOf(DB_GROUP_NODE, logicDbGroup), getId(id, port)));
	}
	
	public boolean removeDasNode(String id) {
		return true;
	}
	
	public String[] getLogicDBs() {
		return null;
	}
	
	public String[] getLogicDB(String logicDbName) {
		return null;
	}
	
	private String getId(String id, int port) {
		return new StringBuilder(id).append(WORKER_ID_PORT_SEPARATOR).append(port).toString();
	}
	
	private List<DasWorker> convertToWorker(List<String> idPorts) {
		List<DasWorker> workers = new ArrayList<DasWorker>();
		for(String idPort: idPorts)
			workers.add(new DasWorker(idPort));
		return workers;
	}

	@Override
	public void initialize() {
		createPath(WORKER);
	}
}
