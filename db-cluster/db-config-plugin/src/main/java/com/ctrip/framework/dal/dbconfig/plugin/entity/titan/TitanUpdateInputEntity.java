package com.ctrip.framework.dal.dbconfig.plugin.entity.titan;

import java.util.List;

/**
 * Created by shenjie on 2019/6/26.
 */
public class TitanUpdateInputEntity {
    private String env;
    private List<TitanUpdateBasicData> dbData;
    private List<MhaInputBasicData> mhaData;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public List<TitanUpdateBasicData> getDbData() {
        return dbData;
    }

    public void setDbData(List<TitanUpdateBasicData> dbData) {
        this.dbData = dbData;
    }

    public List<MhaInputBasicData> getMhaData() {
        return mhaData;
    }

    public void setMhaData(List<MhaInputBasicData> mhaData) {
        this.mhaData = mhaData;
    }

    @Override
    public String toString() {
        return "TitanUpdateInputEntity{" +
                "env='" + env + '\'' +
                ", dbData=" + dbData +
                ", mhaData=" + mhaData +
                '}';
    }
}
