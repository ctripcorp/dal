package com.ctrip.framework.db.cluster.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by shenjie on 2019/3/5.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel {

    private int status;
    private String message;
    private Object result;

    public static ResponseModel successResponse() {
        return successResponse(null);
    }

    public static ResponseModel successResponse(Object result) {
        return new ResponseModel(ResponseStatus.OK.getStatus(), null, result);
    }

    public static ResponseModel failResponse(ResponseStatus responseStatus, String message) {
        return new ResponseModel(responseStatus.getStatus(), message, null);
    }

    public static ResponseModel forbiddenResponse() {
        return new ResponseModel(ResponseStatus.FORBIDDEN.getStatus(), ResponseStatus.FORBIDDEN.getDesc(), null);
    }

    public static ResponseModel forbiddenResponse(final String message) {
        return new ResponseModel(ResponseStatus.FORBIDDEN.getStatus(), ResponseStatus.FORBIDDEN.getDesc(), message);
    }

}
