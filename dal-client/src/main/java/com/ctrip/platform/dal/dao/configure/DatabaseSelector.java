package com.ctrip.platform.dal.dao.configure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DatabaseSelector {
	private List<DataBase> masters;
	private List<DataBase> slaves;
	private String designatedDatasource;
	private DalHA ha;
	private boolean masterOnly;
	private boolean isSelect;
	
	public DatabaseSelector(DalHints hints, List<DataBase> masters, List<DataBase> slaves, boolean masterOnly, boolean isSelect){
		if(hints != null) {
			this.ha = hints.getHA();
			this.designatedDatasource = hints.getString(DalHintEnum.designatedDatabase);
		}
		this.masters = masters;
		this.masterOnly = masterOnly;
		this.slaves = slaves;
		this.isSelect= isSelect;
	}
	
	public String select() throws DalException {
		if(masterOnly || !isSelect)
			return getAvailableDbWithFallback(masters, null);
		
		return getAvailableDbWithFallback(slaves, masters);
	}
	
	private String getAvailableDbWithFallback(List<DataBase> primary, List<DataBase> secondary) throws DalException {
		if(isNullOrEmpty(primary) && isNullOrEmpty(secondary))
			throw new DalException(ErrorCode.NullLogicDbName);
		
		String dbName = getAvailableDb(primary);
		if(dbName != null)
			return dbName;

		dbName = getAvailableDb(secondary);
		if(dbName != null)
			return dbName;
		
		if(null == dbName && (this.ha == null || !this.ha.isOver())){
			StringBuilder sb = new StringBuilder(toDbNames(primary));
			if(isNullOrEmpty(secondary))
				sb.append(", " + toDbNames(secondary));
			throw new DalException(ErrorCode.MarkdownConnection, sb.toString());
		}
		
		return dbName;
	}
	
	private String getAvailableDb(List<DataBase> candidates){
		if(isNullOrEmpty(candidates))
			return null;
		List<String> dbNames = this.selectNotMarkdownDbNames(candidates);
		if(dbNames.isEmpty())
			return null;
		return this.getRandomRealDbName(dbNames);
	}
	
	private String getRandomRealDbName(List<String> dbs){
		if(ha == null || dbs.size() == 1){
			return getWithDesignatedDatasource(dbs);
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
				String selected = getWithDesignatedDatasource(dbNames);
				ha.addDB(selected);
				return selected;
			}
		}
	}
	
	private String getWithDesignatedDatasource(List<String> dbs) {
		if(designatedDatasource != null && dbs.contains(designatedDatasource))
			return designatedDatasource;
		
		int index = (int)(Math.random() * dbs.size());	
		return dbs.get(index);
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
