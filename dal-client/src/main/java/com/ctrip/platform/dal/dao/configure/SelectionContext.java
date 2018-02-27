package com.ctrip.platform.dal.dao.configure;

import java.util.List;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalHA;

public class SelectionContext {
    private String logicDbName;
    private DalHints hints;
    private DalHA ha;
    private String designatedDatabase;
    private String shard;
    private boolean masterOnly;
    private boolean select;
    private List<DataBase> masters;
    private List<DataBase> slaves;
    
    public SelectionContext(String logicDbName, DalHints hints, String shard, 
            boolean isMaster,
            boolean isSelect) {
        this.logicDbName = logicDbName;
        this.hints = hints;
        this.shard = shard;
        if(hints != null) {
            this.ha = hints.getHA();
            this.designatedDatabase = hints.getString(DalHintEnum.designatedDatabase);
        }
        
        this.masterOnly = isMaster;
        this.select = isSelect;
    }
    
    public String getLogicDbName() {
        return logicDbName;
    }

    public String getDesignatedDatabase() {
        return designatedDatabase;
    }

    public DalHA getHa() {
        return ha;
    }
    public List<DataBase> getMasters() {
        return masters;
    }
    
    public List<DataBase> getSlaves() {
        return slaves;
    }
    
    public DalHints getHints() {
        return hints;
    }
    
    public String getShard() {
        return shard;
    }

    public boolean isMasterOnly() {
        return masterOnly;
    }

    public boolean isSelect() {
        return select;
    }

    public void setMasters(List<DataBase> masters) {
        this.masters = masters;
    }

    public void setSlaves(List<DataBase> slaves) {
        this.slaves = slaves;
    }
}
