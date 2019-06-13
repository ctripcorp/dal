package com.ctrip.framework.dal.dbconfig.plugin.entity.cms;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by shenjie on 2019/6/11.
 */
public class CmsRequest {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("request_body")
    private Map<String, Object> requestBody;

    public CmsRequest(String accessToken, Map<String, Object> requestBody) {
        this.accessToken = accessToken;
        this.requestBody = requestBody;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Map<String, Object> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Map<String, Object> requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        return "CmsRequest{" +
                "accessToken='" + accessToken + '\'' +
                ", requestBody=" + requestBody +
                '}';
    }
}
