package com.ctrip.platform.dal.daogen.host.java;

import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

import java.util.ArrayList;
import java.util.List;

public class SpOperationHost {
    private boolean exist;
    private List<JavaParameterHost> parameters = new ArrayList<>();
    // apA Name
    private String basicSpName = "";
    // sp3 Name
    private String batchSpName = "";

    private String type;

    public static SpOperationHost getSpaOperation(String dbName, String tableName, List<StoredProcedure> spNames,
            String operation) throws Exception {
        SpOperationHost host = new SpOperationHost();
        host.exist = true;

        StoredProcedure expectSpa = new StoredProcedure();
        expectSpa.setName(String.format("spA_%s_%s", tableName, operation));
        StoredProcedure expectSp3 = new StoredProcedure();
        expectSp3.setName(String.format("sp3_%s_%s", tableName, operation));
        StoredProcedure currentSp = null;
        int index = -1;

        if ((index = spNames.indexOf(expectSpa)) > -1 && spNames.indexOf(expectSp3) > -1) {// spA、sp3都存在
            host.setBasicSpName(expectSpa.getName());
            host.setBatchSpName(expectSp3.getName());
            host.setType("sp3");
            currentSp = spNames.get(index);
        } else if ((index = spNames.indexOf(expectSpa)) > -1 && spNames.indexOf(expectSp3) < 0) {// 只存在spA
            host.setBasicSpName(expectSpa.getName());
            host.setType("spA");
            currentSp = spNames.get(index);
        } else if (spNames.indexOf(expectSpa) < 0 && (index = spNames.indexOf(expectSp3)) > -1) {// 只存在sp3
            host.setBasicSpName(expectSp3.getName());
            host.setBatchSpName(expectSp3.getName());
            host.setType("sp3");
            currentSp = spNames.get(index);
        } else {
            host.exist = false;
        }

        if (host.exist) {
            List<AbstractParameterHost> params = DbUtils.getSpParams(dbName, currentSp,
                    new JavaSpParamResultSetExtractor(dbName, currentSp.getName()));
            List<JavaParameterHost> realParams = new ArrayList<>();
            for (AbstractParameterHost p : params) {
                realParams.add((JavaParameterHost) p);
            }
            host.parameters = realParams;
        }

        return host;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public List<JavaParameterHost> getParameters() {
        return parameters;
    }

    public void setParameters(List<JavaParameterHost> parameters) {
        this.parameters = parameters;
    }

    public String getBasicSpName() {
        return basicSpName;
    }

    public void setBasicSpName(String basicSpName) {
        this.basicSpName = basicSpName;
    }

    public String getBatchSpName() {
        return batchSpName;
    }

    public void setBatchSpName(String batchSpName) {
        this.batchSpName = batchSpName;
    }

}
