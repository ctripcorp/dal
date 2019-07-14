package com.ctrip.platform.dal.daogen.DynamicDS;

import qunar.tc.qconfig.client.MapConfig;

import java.util.*;

/**
 * Created by taochen on 2019/7/3.
 */
public class TitanDynamicDSAppIDProvider implements DynamicDSAppIDProvider {
    private static final String TITAN_QCONFIG_APPID = "100009917";

    private static final String QCONFIG_KEY = "titan_qconfig_plugin.properties";

    private static final String FREE_PERM_KEY = "permission.valid.free.appIdList";

    private static final int RETRY_TIME = 3;

    @Override
    public List<String> getDynamicDSAppID(String appIDString) {
        List<String> permissionAppIDList = new ArrayList<>();
        List<String> appIDList = Arrays.asList(appIDString.split(","));
        List<String> globalPermissionAppID = getGlobalPermissionAppID();
        permissionAppIDList.addAll(appIDList);
        permissionAppIDList.addAll(globalPermissionAppID);
        return permissionAppIDList;
    }

    private List<String> getGlobalPermissionAppID() {
        MapConfig config = null;
        for (int i = 0; i < RETRY_TIME; ++i) {
            config = MapConfig.get(TITAN_QCONFIG_APPID, QCONFIG_KEY, null);
            if (config != null) {
                    break;
            }
        }
        Map<String, String> map = config.asMap();
        String globalPermissionAppIDStr = map.get(FREE_PERM_KEY);
        String[] globalPermissionAppIDArray = globalPermissionAppIDStr.split(",");
        return Arrays.asList(globalPermissionAppIDArray);
    }
}
