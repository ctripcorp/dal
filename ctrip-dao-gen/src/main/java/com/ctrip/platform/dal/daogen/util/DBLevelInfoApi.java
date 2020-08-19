package com.ctrip.platform.dal.daogen.util;

import com.ctrip.platform.dal.daogen.entity.DBLevelInfo;
import com.ctrip.platform.dal.daogen.entity.DBLevelInfoApiResponse;
import com.ctrip.platform.dal.daogen.entity.DbInfos;
import com.ctrip.platform.dal.daogen.entity.ResponseModel;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.enums.ResponseStatus;
import com.ctrip.platform.dal.daogen.utils.DBInfoApi;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.ctrip.platform.dal.daogen.utils.JsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;

import javax.xml.transform.Result;
import java.util.*;

public class DBLevelInfoApi implements DBInfoApi {
    private static final String DB_INFO_API_URL = "http://osg.ops.ctripcorp.com/api/11310";
    private static final String DB_NAME_BASE_URL = "http://dbcluster.fat2240.qa.nt.ctripcorp.com/console/db/all/dbinfos";
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

    @Override
    public List<DbInfos> getAllDbInfos() {
        Transaction transaction = Cat.newTransaction("db_all_name_base", DB_LEVEL_INFO_API);
        try {
            ResponseModel response = HttpUtil.getJSONEntity(ResponseModel.class, DB_NAME_BASE_URL, new HashMap<>(), HttpMethod.HttpGet);
            if (response.getStatus() == ResponseStatus.OK.getStatus()) {
                transaction.addData(String.valueOf(response.getResult()));
                return response.getResult();
            }
        } catch (Exception e) {
            Cat.logError(e);
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
        return new ArrayList<DbInfos>();
    }
}
