package com.ctrip.platform.daogen.gen;

import java.util.List;

import com.mongodb.DBObject;

public interface Generator {
	
	public boolean generateCode(String projectId);
	
	public void generateAutoSqlCode(List<DBObject> tasks);
	
	public void generateSPCode(List<DBObject> tasks);
	
	public void generateFreeSqlCode(List<DBObject> tasks);

}
