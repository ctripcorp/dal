package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.datasource.log.OperationType;

public class UcsConsistencyValidateContext {

    private OperationType operationType;
    private String ucsValidateResult;
    private long datasourceCreateTime;
    private boolean isSameZone;

    public UcsConsistencyValidateContext(OperationType operationType, String ucsValidateResult, long datasourceCreateTime, boolean isSameZone) {
        this.operationType = operationType;
        this.ucsValidateResult = ucsValidateResult;
        this.datasourceCreateTime = datasourceCreateTime;
        this.isSameZone = isSameZone;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public String getUcsValidateResult() {
        return ucsValidateResult;
    }

    public long getDatasourceCreateTime() {
        return datasourceCreateTime;
    }

    public boolean isSameZone() {
        return isSameZone;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public void setUcsValidateResult(String ucsValidateResult) {
        this.ucsValidateResult = ucsValidateResult;
    }

    public void setDatasourceCreateTime(long datasourceCreateTime) {
        this.datasourceCreateTime = datasourceCreateTime;
    }

    public void setSameZone(boolean sameZone) {
        isSameZone = sameZone;
    }
}
