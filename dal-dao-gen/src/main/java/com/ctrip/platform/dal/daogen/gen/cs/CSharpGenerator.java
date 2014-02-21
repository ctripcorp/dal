package com.ctrip.platform.dal.daogen.gen.cs;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.daogen.gen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.pojo.DatabaseCategory;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;

public class CSharpGenerator extends AbstractGenerator {

	private CSharpGenerator() {

	}

	private static CSharpGenerator instance = new CSharpGenerator();

	public static CSharpGenerator getInstance() {
		return instance;
	}

	@Override
	public void generateBySP(List<GenTask> tasks) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateByFreeSql(List<GenTask> tasks) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateBySqlBuilder(List<GenTask> tasks) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateByTableView(List<GenTaskByTableViewSp> tasks) {
		
		List<CSharpTableHost> tableHosts = new ArrayList<CSharpTableHost>();
		
		//首先为所有表生成DAO
		for(GenTaskByTableViewSp tableViewSp : tasks){
			
			String dbName = tableViewSp.getDb_name();
			String[] tableNames = tableViewSp.getTable_names().split(",");
			String prefix = tableViewSp.getPrefix();
			String suffix  = tableViewSp.getSuffix();
			boolean pagination = tableViewSp.isPagination();
			boolean cud_by_sp = tableViewSp.isCud_by_sp();
			DbServer dbServer = dbServerDao.getDbServerByID(tableViewSp.getServer_id());
			DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
			if(dbServer.getDb_type().equalsIgnoreCase("mysql")){
				dbCategory = DatabaseCategory.MySql;
			}
			
			for(String table : tableNames){
				CSharpTableHost tableHost = new CSharpTableHost();
				String className = table;
				if(null != prefix && !prefix.isEmpty()){
					className = className.substring(prefix.length());
				}
				if(null != suffix && !suffix.isEmpty()){
					className = className + suffix;
				}
				
				tableHost.setNameSpaceEntity(String.format("%s.Entity.DataModel", super.namespace));
				tableHost.setNameSpaceIDao(String.format("%s.Interface.IDao", super.namespace));
				tableHost.setNameSpaceDao(String.format("%s.Dao", super.namespace));
				tableHost.setDatabaseCategory(dbCategory);
				tableHost.setDbSetName(dbName);
				tableHost.setTableName(table);
				tableHost.setClassName(className);
				tableHost.setTable(true);
				tableHost.setSpa(cud_by_sp);
				tableHost.setHasPagination(pagination);
			}
			
		}
		
	}

}
