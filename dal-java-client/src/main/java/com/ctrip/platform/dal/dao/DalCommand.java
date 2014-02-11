package com.ctrip.platform.dal.dao;

import java.sql.SQLException;

public interface DalCommand {
	/**
	 * Execute in same local transaction
	 * @param client
	 * @return true if going to next command, false if stop and commit
	 * @throws SQLException for roll back
	 */
	boolean execute(DalClient client) throws SQLException;
}
