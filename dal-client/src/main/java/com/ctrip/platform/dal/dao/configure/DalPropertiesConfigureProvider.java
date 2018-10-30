package com.ctrip.platform.dal.dao.configure;

import java.util.Map;

public interface DalPropertiesConfigureProvider {
    Map<Integer, ErrorCodeInfo> getErrorCodes();
}
