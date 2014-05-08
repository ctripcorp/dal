package com.ctrip.platform.performance.daos;

public class DefaultJpaDao {
	private static DefaultJpaDao dao = null;
	private DefaultJpaDao() {}
	public static DefaultJpaDao instance(){
		if(null == dao)
			dao = new DefaultJpaDao();
		return dao;
	}
}
