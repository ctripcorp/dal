package com.ctrip.platform.dal.dao.configure.dalproperties;

import com.ctrip.framework.dal.cluster.client.base.UnsupportedListenable;
import com.ctrip.platform.dal.common.enums.ImplicitAllShardsSwitch;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.configure.ErrorCodeInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractDalPropertiesLocator implements DalPropertiesLocator {
    private static final String SEPARATOR_SEMICOLON = ";";
    private static final String SEPARATOR_COMMA = ",";
    private static final int DEFAULT_CLEAR_INTERVAL_IN_SECONDS = -1;

    private AtomicReference<Map<String, ErrorCodeInfo>> errorCodesRef = new AtomicReference<>();

    @Override
    public void setProperties(Map<String, String> properties) {
        String errorCodes = null;
        if (properties != null && !properties.isEmpty()) {
            String key = getErrorCodesKey();
            errorCodes = properties.get(key);
        }

        setErrorCodes(errorCodes);
    }

    protected abstract String getErrorCodesKey();

    protected abstract String[] getDefaultErrorCodes();

    @Override
    public TableParseSwitch getTableParseSwitch() {
        throw new UnsupportedOperationException("getTableParseSwitch not supported.");
    }

    @Override
    public Map<String, ErrorCodeInfo> getErrorCodes() {
        Map<String, ErrorCodeInfo> map = errorCodesRef.get();
        if (map == null)
            return new HashMap<>();

        return new HashMap<>(map); // avoid origin data being modified outside
    }

    @Override
    public ImplicitAllShardsSwitch getImplicitAllShardsSwitch() {
        throw new UnsupportedOperationException("getImplicitAllShardsSwitch not supported.");
    }

    protected void setErrorCodes(String errorCodes) {
        String[] defaultErrorCodes = getDefaultErrorCodes();

        if (errorCodes == null || errorCodes.isEmpty()) {
            setDefaultErrorCodes(defaultErrorCodes);
            return;
        }

        setCustomErrorCodes(errorCodes, defaultErrorCodes);
    }

    private void setDefaultErrorCodes(String[] defaultErrorCodes) {
        Map<String, ErrorCodeInfo> map = getDefaultParameters(defaultErrorCodes);
        errorCodesRef.set(map);
    }

    private Map<String, ErrorCodeInfo> getDefaultParameters(String[] defaultErrorCodes) {
        Map<String, ErrorCodeInfo> map = new HashMap<>();
        if (defaultErrorCodes == null || defaultErrorCodes.length == 0)
            return map;

        for (String errorCode : defaultErrorCodes) {
            if (!map.containsKey(errorCode)) {
                ErrorCodeInfo info = getErrorCodeInfo(errorCode);
                map.put(errorCode, info);
            }
        }

        return map;
    }

    private void setCustomErrorCodes(String errorCodes, String[] defaultErrorCodes) {
        Map<String, ErrorCodeInfo> map = new HashMap<>();
        String[] array = errorCodes.split(SEPARATOR_SEMICOLON);
        for (String errorCode : array) {
            String[] item = errorCode.split(SEPARATOR_COMMA);
            if (item == null || item.length == 0)
                continue;

            ErrorCodeInfo info = null;

            if (item.length == 1) {
                info = getErrorCodeInfo(item[0]);
            } else if (item.length == 2) {
                info = getParameters(item);
            }

            if (!map.containsKey(item[0])) {
                map.put(item[0], info);
            }
        }

        if (map.size() == 0) {
            map = getDefaultParameters(defaultErrorCodes);
        }

        errorCodesRef.set(map);
    }

    private ErrorCodeInfo getParameters(String[] array) {
        String errorCode = array[0];
        ErrorCodeInfo info = getErrorCodeInfo(errorCode);

        int interval = -1;
        boolean success = true;
        try {
            interval = Integer.parseInt(array[1]);
        } catch (Throwable e) {
            success = false;
        }

        if (!success)
            return info;

        info.setIntervalInSeconds(interval);
        return info;
    }

    private ErrorCodeInfo getErrorCodeInfo(String errorCode) {
        ErrorCodeInfo info = new ErrorCodeInfo();
        info.setErrorCode(errorCode);
        info.setIntervalInSeconds(DEFAULT_CLEAR_INTERVAL_IN_SECONDS);
        return info;
    }

    @Override
    public String getClusterInfoQueryUrl() {
        throw new UnsupportedOperationException("getClusterInfoQueryUrl not supported.");
    }

    @Override
    public boolean localizedForDrc(String situation, boolean isUpdateOperation) {
        throw new UnsupportedOperationException("localizedForDrc not supported.");
    }

    @Override
    public String getProperty(String name) {
        throw new UnsupportedOperationException("getProperty not supported.");
    }

    @Override
    public String getConnectionStringMysqlApiUrl() {
        throw new UnsupportedOperationException("getConnectionStringMysqlApiUrl not supported.");
    }

    @Override
    public String getStatementInterceptor() {
        throw new UnsupportedOperationException("getStatementInterceptor not supported.");
    }
}
