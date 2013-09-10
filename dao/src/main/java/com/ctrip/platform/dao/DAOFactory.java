package com.ctrip.platform.dao;


public final class DAOFactory {
	
	public static PersonDAO createPersonDAO(){
		return new PersonDAO();
	}
	
	public static SysDalTestSPDAO createSysDalTestSPDAO(){
		return new SysDalTestSPDAO();
	}
	
	public static FreeSQLPersonDAO createFreeSQLPersonDAO(){
		return new FreeSQLPersonDAO();
	}
	
}
