package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.daogen.enums.ResponseStatus;

import java.util.List;

public class ResponseModel {

    private int status;
    private String message;
    private List<DbInfos> result;

    public ResponseModel(){}

    public ResponseModel(int status, String message, List<DbInfos> result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public static ResponseModel successResponse() {
        return successResponse(null);
    }

    public static ResponseModel successResponse(List<DbInfos> result) {
        return new ResponseModel(ResponseStatus.OK.getStatus(), null, result);
    }

    public static ResponseModel forbiddenResponse() {
        return new ResponseModel(ResponseStatus.FORBIDDEN.getStatus(), ResponseStatus.FORBIDDEN.getDesc(), null);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DbInfos> getResult() {
        return result;
    }

    public void setResult(List<DbInfos> result) {
        this.result = result;
    }
}
