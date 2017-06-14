package com.ctrip.platform.dal.daogen.host.csharp;

import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

import java.util.ArrayList;
import java.util.List;

public class CSharpSpaOperationHost {

    private boolean exist;

    private List<CSharpParameterHost> parameters;

    private String methodName;

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public List<CSharpParameterHost> getParameters() {
        return parameters;
    }

    public void setParameters(List<CSharpParameterHost> parameters) {
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public static CSharpSpaOperationHost getSpaOperation(String dbName, String tableName, List<StoredProcedure> spNames,
            String operation) throws Exception {
        CSharpSpaOperationHost host = new CSharpSpaOperationHost();

        StoredProcedure expectSpa = new StoredProcedure();
        expectSpa.setName(String.format("spA_%s_%s", tableName, operation));
        StoredProcedure expectSp3 = new StoredProcedure();
        expectSp3.setName(String.format("sp3_%s_%s", tableName, operation));
        StoredProcedure currentSp = null;
        int index = -1;

        if ((index = spNames.indexOf(expectSpa)) > 0) {
            host.exist = true;
            host.methodName = expectSpa.getName();
            currentSp = spNames.get(index);
        } else if ((index = spNames.indexOf(expectSp3)) > 0) {
            host.exist = true;
            host.methodName = expectSp3.getName();
            currentSp = spNames.get(index);
        } else {
            host.exist = false;
        }

        if (host.exist) {
            List<AbstractParameterHost> parameters =
                    DbUtils.getSpParams(dbName, currentSp, new CsharpSpParamResultSetExtractor());
            List<CSharpParameterHost> realParams = new ArrayList<CSharpParameterHost>();
            for (AbstractParameterHost _host : parameters) {
                realParams.add((CSharpParameterHost) _host);
            }

            host.setParameters(realParams);
        }

        return host;
    }
}

