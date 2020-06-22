package com.ctrip.platform.dal.dao.configure;


public  abstract class AbstractDataSourceConfigure implements IDataSourceConfigure {

    public Boolean getTestWhileIdle(){
        return null;
    }

    public Boolean getTestOnBorrow(){
        return null;
    }

    public Boolean getTestOnReturn(){
        return null;
    }

    public String getValidationQuery(){
        return null;
    }

    public Integer getValidationQueryTimeout(){
        return null;
    }

    public Long getValidationInterval(){
        return null;
    }

    public Integer getTimeBetweenEvictionRunsMillis(){
        return null;
    }

    public Integer getMinEvictableIdleTimeMillis(){
        return null;
    }

    public Integer getMaxAge(){
        return null;
    }

    public Integer getMaxActive(){
        return null;
    }

    public Integer getMinIdle(){
        return null;
    }

    public Integer getMaxWait(){
        return null;
    }

    public Integer getInitialSize(){
        return null;
    }

    public Integer getRemoveAbandonedTimeout(){
        return null;
    }

    public Boolean getRemoveAbandoned(){
        return null;
    }

    public Boolean getLogAbandoned(){
        return null;
    }

    public String getConnectionProperties(){
        return null;
    }

    public String getValidatorClassName(){
        return null;
    }

    public String getInitSQL(){
        return null;
    }

    public String getJdbcInterceptors(){
        return null;
    }

    public Integer getSessionWaitTimeout() {
        return null;
    }

}
