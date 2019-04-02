package com.ctrip.framework.dal.dbconfig.plugin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by lzyan on 2017/8/25.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Soa2KeyResponseStatus {

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
