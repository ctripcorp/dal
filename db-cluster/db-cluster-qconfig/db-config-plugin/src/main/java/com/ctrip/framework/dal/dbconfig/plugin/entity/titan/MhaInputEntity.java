package com.ctrip.framework.dal.dbconfig.plugin.entity.titan;


import java.util.List;

public class MhaInputEntity {
    //field
    private String env;
    private List<MhaInputBasicData> data;

    public MhaInputEntity() {
    }
    public MhaInputEntity(String env, List<MhaInputBasicData> data) {
        this.env = env;
        this.data = data;
    }

    //getter/setter
    public String getEnv() {
      return env;
    }
    public void setEnv(String env) {
      this.env = env;
    }

    public List<MhaInputBasicData> getData() {
      return data;
    }
    public void setData(List<MhaInputBasicData> data) {
      this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MhaInputEntity{");
        sb.append("env='").append(env).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }

}
