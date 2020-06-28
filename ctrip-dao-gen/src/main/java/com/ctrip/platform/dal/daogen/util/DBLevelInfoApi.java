package com.ctrip.platform.dal.daogen.util;

import com.ctrip.platform.dal.daogen.entity.DBLevelInfo;
import com.ctrip.platform.dal.daogen.entity.DBLevelInfoApiResponse;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.utils.DBInfoApi;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.ctrip.platform.dal.daogen.utils.JsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBLevelInfoApi implements DBInfoApi {
    private static final String DB_INFO_API_URL = "http://osg.ops.ctripcorp.com/api/11310";
    private static final String ACCESS_TOKEN = "726e294a9f420492d8a29e7d302817ca";
    private static final String DB_LEVEL_INFO = "dbLevelInfo";
    private static final String DB_LEVEL_INFO_API = "dbLevelInfoAPI";
    private static final String DB_TYPE = "db_type";

    @Override
    public List<DBLevelInfo> getDBLevelInfo(String dbType) {
        Transaction transaction = Cat.newTransaction(DB_LEVEL_INFO, DB_LEVEL_INFO_API);
        Map<String, Object> queries = Maps.newHashMap();
        queries.put(DB_TYPE, dbType);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("access_token", ACCESS_TOKEN);
        parameters.put("request_body", JsonUtils.toJson(queries));

        try {
            DBLevelInfoApiResponse response = HttpUtil.getJSONEntity(DBLevelInfoApiResponse.class, DB_INFO_API_URL, parameters, HttpMethod.HttpPost);
            if (response.isSuccess()) {
                transaction.addData(String.valueOf(response.getData().size()));
                return response.getData();
            }
        } catch (Exception e) {
            Cat.logError(e);
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
        return null;
    }
}
