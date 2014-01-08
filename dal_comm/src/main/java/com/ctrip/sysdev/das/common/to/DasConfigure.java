package com.ctrip.sysdev.das.common.to;

import java.util.List;

public class DasConfigure {
	private List<String> port;
	private List<MasterLogicDB> db;
	private List<LogicDbGroup> db_group;
	private List<DasNode> node;
	private List<Deployment> deployment;
}
