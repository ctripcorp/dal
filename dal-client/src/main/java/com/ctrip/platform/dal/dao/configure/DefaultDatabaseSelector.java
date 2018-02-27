package com.ctrip.platform.dal.dao.configure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DefaultDatabaseSelector implements DatabaseSelector, DalComponent {

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
    }

    @Override
	public String select(SelectionContext context) throws DalException {
        String designatedDatasource = context.getDesignatedDatabase();
        DalHA ha = context.getHa();
        
        List<DataBase> primary;
        List<DataBase> secondary = null;

        
        if(context.getHints().is(DalHintEnum.slaveOnly)) {
            primary = context.getSlaves();
        } else if(context.isMasterOnly() || !context.isSelect()) {
		    primary = context.getMasters();
		} else {
		    primary = context.getSlaves();
		    secondary = context.getMasters();
		}

		if(isNullOrEmpty(primary) && isNullOrEmpty(secondary))
			throw new DalException(ErrorCode.NullLogicDbName);
		
		if(designatedDatasource != null){
			if(!DalStatusManager.containsDataSourceStatus(designatedDatasource))
				throw new DalException(ErrorCode.InvalidDatabaseKeyName, designatedDatasource);
		
			if(MarkdownManager.isMarkdown(designatedDatasource))
				throw new DalException(ErrorCode.MarkdownConnection, designatedDatasource);
			
			if(ha != null && ha.contains(designatedDatasource)) {
				ha.setOver(true);
				throw new DalException(ErrorCode.NoMoreConnectionToFailOver);
			}
			
			if(containsDesignatedDatasource(designatedDatasource, primary))
				return designatedDatasource;

			if(containsDesignatedDatasource(designatedDatasource, secondary))
				return designatedDatasource;
			
			throw new DalException(ErrorCode.InvalidDatabaseKeyName, designatedDatasource);
		}
		
		String dbName = getAvailableDb(ha, primary);
		if(dbName != null)
			return dbName;

		dbName = getAvailableDb(ha, secondary);
		if(dbName != null)
			return dbName;
		
		if(ha != null) {
			ha.setOver(true);
			throw new DalException(ErrorCode.NoMoreConnectionToFailOver);
		}

		StringBuilder sb = new StringBuilder(toDbNames(primary));
		if(isNullOrEmpty(secondary))
			sb.append(", " + toDbNames(secondary));
		
		throw new DalException(ErrorCode.MarkdownConnection, sb.toString());
	}
	
	private String getAvailableDb(DalHA ha, List<DataBase> candidates) throws DalException{
		if(isNullOrEmpty(candidates))
			return null;
		List<String> dbNames = this.selectValidDbNames(candidates);
		if(dbNames.isEmpty())
			return null;
		return this.getRandomRealDbName(ha, dbNames);
	}
	
	private String getRandomRealDbName(DalHA ha, List<String> dbs) throws DalException{
		if(ha == null|| dbs.size() == 1){
			return choseByRandom(dbs);
		}else{
			List<String> dbNames = new ArrayList<String>();
			for (String database : dbs) {
				if(!ha.contains(database))
					dbNames.add(database);
			}
			if(dbNames.isEmpty()){
				return null;
			}else{
				String selected = choseByRandom(dbNames);
				ha.addDB(selected);
				return selected;
			}
		}
	}
	
	private String choseByRandom(List<String> dbs) throws DalException {
		int index = (int)(Math.random() * dbs.size());	
		return dbs.get(index);
	}
	
	private List<String> selectValidDbNames(List<DataBase> dbs){
		List<String> dbNames = new ArrayList<String>();
		if(!this.isNullOrEmpty(dbs)){
			for (DataBase database : dbs) {
				if(MarkdownManager.isMarkdown(database.getConnectionString()))
					continue;

				dbNames.add(database.getConnectionString());
			}
		}
		return dbNames;
	}
	
	private boolean containsDesignatedDatasource(String designatedDatasource, List<DataBase> dbs){
		if(isNullOrEmpty(dbs))
			return false;

		for (DataBase database : dbs)
			if(designatedDatasource.equals(database.getConnectionString()))
				return true;

		return false;
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
