package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.ErrorCodesUtil;

import java.util.HashMap;
import java.util.Map;

public class SqlServerDalPropertiesConfigureProvider implements DalPropertiesConfigureProvider {
    private IDalPropertiesProvider dalPropertiesProvider = DalElementFactory.DEFAULT.getDalPropertiesProvider();
    private final Object LOCK = new Object();
    private volatile Map<String, String> dalProperties = null;

    private ErrorCodesUtil errorCodesUtil = ErrorCodesUtil.getInstance();
    private volatile Map<Integer, ErrorCodeInfo> errorCodes = null;
    private static final String JAVA_SQL_SERVER_ERROR_CODES = "JavaSqlServerErrorCodes";

    public SqlServerDalPropertiesConfigureProvider() {
        initializeDalProperties();
    }

    private void initializeDalProperties() {
        if (dalProperties == null) {
            synchronized (LOCK) {
                if (dalProperties == null) {
                    dalProperties = dalPropertiesProvider.getProperties();
                    errorCodes = getErrorCodes(dalProperties);
                    dalPropertiesProvider.addPropertiesChangedListener(new IDalPropertiesChanged() {
                        @Override
                        public void onChanged(Map<String, String> map) {
                            refreshErrorCodes(map);
                        }
                    });
                }
            }
        }
    }

    @Override
    public Map<Integer, ErrorCodeInfo> getErrorCodes() {
        return new HashMap<>(errorCodes);
    }

    private Map<Integer, ErrorCodeInfo> getErrorCodes(Map<String, String> properties) {
        String codes = null;
        if (properties != null && !properties.isEmpty()) {
            codes = properties.get(JAVA_SQL_SERVER_ERROR_CODES);
        }

        return errorCodesUtil.convertErrorCodes(codes);
    }

    private void refreshErrorCodes(Map<String, String> properties) {
        if (properties == null || properties.isEmpty())
            return;

        String codes = properties.get(JAVA_SQL_SERVER_ERROR_CODES);
        Map<Integer, ErrorCodeInfo> temp = errorCodesUtil.convertErrorCodes(codes);
        Map<Integer, ErrorCodeInfo> dict = getErrorCodes();
        Boolean errorCodesEquals = errorCodesUtil.errorCodesEquals(temp, dict);
        if (errorCodesEquals) {
            return;
        }

        errorCodes = temp;
    }

}
