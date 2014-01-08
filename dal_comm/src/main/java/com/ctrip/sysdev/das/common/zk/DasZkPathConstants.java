package com.ctrip.sysdev.das.common.zk;

public interface DasZkPathConstants {
	// Start of static configuration
	
	// Logic DB to physical DB mapping, sub nodes are logic DB
	String DB = "/dal/das/configure/db";
	
	// Logic DB to logic DB group mapping, sub nodes are logic DB group
	String DB_GROUP = "/dal/das/configure/db_group";

	// Logic DB group to Das worker mapping, sub nodes are worker ip. logic DB group
	String DEPLOYMENT = "/dal/das/configure/deployment";

	// Under port, the node name is port number , the value is logic DB name
	String PORT = "/dal/das/configure/port";

	// Under node, the node name is server IP
	String NODE = "/dal/das/configure/node";

	// End of static configuration
	
	// Start of runtime registration

	// When controller started, it will check CONTROLLER/ip to see if it is already existed.
	// If not, it will create ephemeral child node. It will also create WORKERS/ip to host workers
	String CONTROLLER = "/dal/das/instance/controller";

	// When worker started, it will check worker/ip/port to see if node exists.
	// If not, it will create ephemeral child node. 
	String WORKER = "/dal/das/instance/worker";
	
	String DB_NODE = "/dal/das/instance/db_node";
	
	String DB_GROUP_NODE = "/dal/das/instance/db_group_node";
	
	// Start of runtime registration
	
	// ZK path separator
	String SEPARATOR = "/";
	
	// Worker id port separator. e.g. 123.456.789.1:1234
	String WORKER_ID_PORT_SEPARATOR = ":";


	// Value separator
	String VALUE_SEPARATOR = ",";

	// Driver and JDBC URL is under logic DB node. e.g. /dal/das/configure/db/testdb
	String DRIVER = "driver";
	String JDBC_URL = "jdbcUrl";
	
	String STARTING_HEAP_SIZE = "startingHeapSize";
	String DEFAULT_STARTING_HEAP_SIZE = String.valueOf(1024);
	String MAX_HEAP_SIZE = "maxHeapSize";
	String DEFAULT_MAX_HEAP_SIZE = String.valueOf(4096);

	// Worker's working directory
	String DIRECTORY = "directory";
	String USER_HOME = "user.home";
	
	String EMPTY_VALUE = "";
	String DEPLOYMENT_SEPARATOR = ":";
	String DEPLOYMENT_VALUE_SEPARATOR = ",";
	String SHARED = "shared";
	String DEDICATE = "dedicate";
	
}
