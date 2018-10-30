package com.ctrip.platform.dal.dao.configure;

import java.util.Map;

public interface IDalPropertiesChanged {
    void onChanged(Map<String, String> map);
}
