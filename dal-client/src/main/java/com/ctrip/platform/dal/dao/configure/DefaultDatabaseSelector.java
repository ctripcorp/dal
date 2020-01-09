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
	public DataBase select(SelectionContext context) throws DalException {
        String designatedDataBaseString = context.getDesignatedDatabase();
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
		
		if(designatedDataBaseString != null){
			if(!DalStatusManager.containsDataSourceStatus(designatedDataBaseString))
				throw new DalException(ErrorCode.InvalidDatabaseKeyName, designatedDataBaseString);
		
			if(MarkdownManager.isMarkdown(designatedDataBaseString))
				throw new DalException(ErrorCode.MarkdownConnection, designatedDataBaseString);
			
			if(ha != null && ha.contains(designatedDataBaseString)) {
				ha.setOver(true);
				throw new DalException(ErrorCode.NoMoreConnectionToFailOver);
			}

			DataBase designatedDataBase = getDesignatedDataBase(designatedDataBaseString, primary);
			if (designatedDataBase != null)
				return designatedDataBase;

			designatedDataBase = getDesignatedDataBase(designatedDataBaseString, secondary);
			if (designatedDataBase != null)
				return designatedDataBase;
			
			throw new DalException(ErrorCode.InvalidDatabaseKeyName, designatedDataBaseString);
		}

		DataBase db = getAvailableDb(ha, primary);
		if(db != null)
			return db;

		db = getAvailableDb(ha, secondary);
		if(db != null)
			return db;
		
		if(ha != null) {
			ha.setOver(true);
			throw new DalException(ErrorCode.NoMoreConnectionToFailOver);
		}

		StringBuilder sb = new StringBuilder(toDbNames(primary));
		if(isNullOrEmpty(secondary))
			sb.append(", " + toDbNames(secondary));
		
		throw new DalException(ErrorCode.MarkdownConnection, sb.toString());
	}
	
	private DataBase getAvailableDb(DalHA ha, List<DataBase> candidates) throws DalException{
		if(isNullOrEmpty(candidates))
			return null;
		List<DataBase> availableDbs = this.selectValidDataBases(candidates);
		if(availableDbs.isEmpty())
			return null;
		return this.getRandomRealDbName(ha, availableDbs);
	}
	
	private DataBase getRandomRealDbName(DalHA ha, List<DataBase> dbs) throws DalException{
		if(ha == null|| dbs.size() == 1){
			return choseByRandom(dbs);
		}else{
			List<DataBase> databases = new ArrayList<>();
			for (DataBase database : dbs) {
				if(!ha.contains(database.getConnectionString()))
					databases.add(database);
			}
			if(databases.isEmpty()){
				return null;
			}else{
				DataBase selected = choseByRandom(databases);
				ha.addDB(selected.getConnectionString());
				return selected;
			}
		}
	}
	
	private DataBase choseByRandom(List<DataBase> dbs) throws DalException {
		int index = (int)(Math.random() * dbs.size());	
		return dbs.get(index);
	}
	
	private List<DataBase> selectValidDataBases(List<DataBase> dbs){
		List<DataBase> validDbs = new ArrayList<>();
		if(!this.isNullOrEmpty(dbs)){
			for (DataBase database : dbs) {
				if(MarkdownManager.isMarkdown(database.getConnectionString()))
					continue;

				validDbs.add(database);
			}
		}
		return validDbs;
	}
	
	private DataBase getDesignatedDataBase(String designatedDataBaseString, List<DataBase> dbs){
		if(isNullOrEmpty(dbs))
			return null;

		for (DataBase database : dbs)
			if(designatedDataBaseString.equals(database.getConnectionString()))
				return database;

		return null;
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
