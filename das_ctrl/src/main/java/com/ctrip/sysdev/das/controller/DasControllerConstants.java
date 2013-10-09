package com.ctrip.sysdev.das.controller;

public interface DasControllerConstants {
	// ZK path separator
	String SEPARATOR = "/";
	
	// Logic DB to physical DB mapping, sub nodes are logic DB
	String DB = "/dal/das/configure/db";
	
	// Driver and JDBC URL is under logic DB node. e.g. /dal/das/configure/db/testdb
	String DRIVER = "driver";
	String JDBC_URL = "jdbcUrl";
	
	// Under port, the node name is port number , the value is logic DB name
	String PORT = "/dal/das/configure/port";

	// Under node, the node name is server IP
	String NODE = "/dal/das/configure/node";

	// The value of startup is how we start a DAS worker
	String START_CMD = "/dal/das/configure/startup";

	// When controller started, it will check CONTROLLER/ip to see if it is already existed.
	// If not, it will create ephemeral child node. It will also create WORKERS/ip to host workers
	String CONTROLLER = "/dal/das/instance/controller";

	// When worker started, it will check worker/ip/port to see if node exists.
	// If not, it will create ephemeral child node. 
	String WORKER = "/dal/das/instance/worker";
}
