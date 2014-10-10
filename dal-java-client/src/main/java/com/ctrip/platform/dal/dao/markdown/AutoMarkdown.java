package com.ctrip.platform.dal.dao.markdown;

public interface AutoMarkdown {
	/**
	 * Collect the exception. 
	 * If the specified exception has been collected successfully
	 * return true, else return false.
	 * @param mark
	 * 		mark information
	 */
	void collectException(MarkContext mark);
}
