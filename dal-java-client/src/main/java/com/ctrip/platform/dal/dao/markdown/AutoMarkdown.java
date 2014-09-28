package com.ctrip.platform.dal.dao.markdown;

public interface AutoMarkdown {
	/**
	 * Collect the exception. 
	 * If the specified exception has been collected successfully
	 * return true, else return false.
	 * @param mark
	 * 		mark information
	 */
	void collectException(Mark mark);
	
	/**
	 * Judge the database name is marked down or not
	 * @param dbname
	 * 		All in one key
	 * @return
	 */
	boolean isMarkdown(String key);
	
	/**
	 * Mark the specified database up 
	 * @param name
	 * 		All in one key
	 */
	void markup(String key);
}
