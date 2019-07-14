package com.ctrip.platform.dal.daogen.DynamicDS;

import java.util.List;

/**
 * Created by taochen on 2019/7/3.
 */
public interface DynamicDSAppIDProvider {
    public List<String> getDynamicDSAppID(String permissions);
}
