package com.ctrip.platform.dal.daogen.util;

import com.ctrip.platform.dal.daogen.entity.TitanApiResponse;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.utils.AllInOneKeyApi;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.dianping.cat.Cat;

import java.util.List;

public class TitanApi implements AllInOneKeyApi {
    private static final String TITAN_API = "http://qconfig.ctripcorp.com/plugins/titan/whitelist/listTitanKey?dbName=%s";

    @Override
    public List<String> getAllInOneKeys(String dbName) {
        String formatUrl = String.format(TITAN_API, dbName.toLowerCase());
        try {
            TitanApiResponse response = HttpUtil.getJSONEntity(TitanApiResponse.class, formatUrl, null, HttpMethod.HttpGet);
            if (response.getStatus() == 0) {
                return response.getData();
            }
        } catch (Exception e) {
            Cat.logError(e);
        }
        return null;
    }
}
