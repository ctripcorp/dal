package com.ctrip.framework.db.cluster.crypto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SOAResponseStatus {

    @JsonProperty("Timestamp")
    private String timestamp;

    @JsonProperty("Ack")
    private String ack;

    @JsonProperty("Errors")
    private List<String> errors;

    @JsonProperty("Extension")
    private List<String> extension;

    //setter/getter
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAck() {
        return ack;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getExtension() {
        return extension;
    }

    public void setExtension(List<String> extension) {
        this.extension = extension;
    }

}
