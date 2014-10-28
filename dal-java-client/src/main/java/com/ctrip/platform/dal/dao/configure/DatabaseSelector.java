package com.ctrip.platform.dal.dao.configure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DatabaseSelector {
	private List<DataBase> masters;
	private List<DataBase> slaves;
	private DalHA ha;
	private boolean isSelect;
	
	public DatabaseSelector(DalHA ha, List<DataBase> masters, List<DataBase> slaves, boolean isSelect){
		this.ha = ha;
		this.masters = masters;
		this.slaves = slaves;
		this.isSelect= isSelect;
	}
	
	public String select() throws DalException{
		if(this.isSelect){
			List<String> dbNames = this.selectNotMarkdownDbNames(this.slaves);
			if(dbNames.isEmpty()){
				dbNames = this.selectNotMarkdownDbNames(this.masters);
			}
			if(!(this.isNullOrEmpty(this.slaves) && this.isNullOrEmpty(this.masters))){
				if(dbNames.isEmpty()){
					throw new DalException(ErrorCode.MarkdownConnection, 
							this.toDbNames(this.masters) + ", " + this.toDbNames(this.slaves));
				}
				return this.getRandomRealDbName(dbNames);
			}else{
				throw new DalException(ErrorCode.NullLogicDbName);
			}
		} else{
			List<String> dbNames = this.selectNotMarkdownDbNames(this.masters);
			if(!this.isNullOrEmpty(this.masters)){
				if(dbNames.isEmpty())
					throw new DalException(ErrorCode.MarkdownConnection, this.toDbNames(this.masters));
				return this.getRandomRealDbName(dbNames);
			}
			else{
				throw new DalException(ErrorCode.NullLogicDbName);
			}
		}
	}
	
	private String getRandomRealDbName(List<String> dbs){
		if(ha == null || dbs.size() == 1){
			int index = (int)(Math.random() * dbs.size());	
			return dbs.get(index);
		}else{
			List<String> dbNames = new ArrayList<String>();
			for (String database : dbs) {
				if(!ha.contains(database))
					dbNames.add(database);
			}
			if(dbNames.isEmpty()){
				ha.setOver(true);
				return null;
			}else{
				int index = (int)(Math.random() * dbNames.size());
				ha.addDB(dbNames.get(index));
				return dbNames.get(index);
			}
		}
	}
	
	private List<String> selectNotMarkdownDbNames(List<DataBase> dbs){
		List<String> dbNames = new ArrayList<String>();
		if(!this.isNullOrEmpty(dbs)){
			for (DataBase database : dbs) {
				if(!MarkdownManager.isMarkdown(database.getConnectionString()))
				{
					dbNames.add(database.getConnectionString());
				}
			}
		}
		return dbNames;
	}
	
	private String toDbNames(List<DataBase> dbs){
		if(this.isNullOrEmpty(dbs)){
			return "";
		}
		List<String> dbNames = new ArrayList<String>();
		for (DataBase database : dbs) {
			dbNames.add(database.getConnectionString());
		}
		return StringUtils.join(dbNames, ",");
	}

	@SuppressWarnings("rawtypes")
	private boolean isNullOrEmpty(List list){
		return list == null || list.isEmpty();
	}
}
