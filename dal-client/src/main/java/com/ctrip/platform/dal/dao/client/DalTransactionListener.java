package com.ctrip.platform.dal.dao.client;

/**
 * The listener will be invoked on certain transaction event.
 * No matter the invocation fails or not, the commit or rollback will not be interrupted.
 * And all of the registered listeners will be invoked by the sequence they registered.
 * 
 * @author jhhe
 */
public interface DalTransactionListener {
	void beforeCommit();
	void beforeRollback();
	void afterCommit();
	void afterRollback();
}
