package com.ctrip.framework.dal.dbconfig.plugin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lzyan on 2017/8/25.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Soa2KeyResponse {
    //{"Signature":"1234567890123456","ReturnCode":0,"ResponseStatus":{"Timestamp":"\/Date(1503661407744+0800)\/","Ack":"Success","Errors":[],"Extension":[]}}
    //{"Signature":"1234567890123456","ReturnCode":2,"ResponseStatus":{"Timestamp":"\/Date(1503661407744+0800)\/","Ack":"Success","Errors":[],"Build":null,"Version":null,"Extension":[]},"Message":"error detail info"}

    @JsonProperty("Signature")
    private String signature;

    @JsonProperty("ReturnCode")
    private int returnCode;

    @JsonProperty("ResponseStatus")
    private Soa2KeyResponseStatus responseStatus;

    @JsonProperty("Message")
    private String message;

    //setter/getter
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getReturnCode() {
        return returnCode;
    }
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public Soa2KeyResponseStatus getResponseStatus() {
        return responseStatus;
    }
    public void setResponseStatus(Soa2KeyResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }


}
