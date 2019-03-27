package com.ctrip.framework.db.cluster.crypto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecretKeyResponse {
    //{"Signature":"1234567890123456","ReturnCode":0,"ResponseStatus":{"Timestamp":"\/Date(1503661407744+0800)\/","Ack":"Success","Errors":[],"Extension":[]}}
    //{"Signature":"1234567890123456","ReturnCode":2,"ResponseStatus":{"Timestamp":"\/Date(1503661407744+0800)\/","Ack":"Success","Errors":[],"Build":null,"Version":null,"Extension":[]},"Message":"error detail info"}

    @JsonProperty("Signature")
    private String signature;

    @JsonProperty("ReturnCode")
    private int returnCode;

    @JsonProperty("ResponseStatus")
    private SOAResponseStatus responseStatus;

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

    public SOAResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(SOAResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
