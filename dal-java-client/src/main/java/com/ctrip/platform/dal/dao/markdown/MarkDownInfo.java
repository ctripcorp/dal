package com.ctrip.platform.dal.dao.markdown;

import java.util.HashMap;
import java.util.Map;


public class MarkDownInfo {	
	public static final String KEY = "arch.dal.markdown.info";
	
	private static String MarkDown_AllInOneKey = "AllInOneKey";
	private String allInOneKey;
	
	private static String MarkDown_MarkDownPolicy = "MarkDownPolicy";
	private MarkDownPolicy policy;
	
	private static String MarkDown_Status = "Status";
	private String status;

	private static String MarkDown_SamplingDuration = "SamplingDuration";
	private Long duration;
	
	private static String MarkDown_MarkDownReason = "Reason";
	private MarkDownReason reason;
	
	public static final String CLIENT = "Client";
	private String version;
	
	private long total = 0;
	private long fail = 0;

	public MarkDownInfo(String allinoneKey,String version, MarkDownPolicy policy, long duration){
		this.allInOneKey = allinoneKey;
		this.version = "Java " + version;
		this.policy = policy;
		this.duration = duration;
	}
	
	public String getAllInOneKey() {
		return allInOneKey;
	}
	public void setAllInOneKey(String allInOneKey) {
		this.allInOneKey = allInOneKey;
	}
	public MarkDownPolicy getPolicy() {
		return policy;
	}
	public void setPolicy(MarkDownPolicy policy) {
		this.policy = policy;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public MarkDownReason getReason() {
		return reason;
	}
	public void setReason(MarkDownReason reason) {
		this.reason = reason;
	}
	
	public void setTotal(long total){
		this.total = total;
	}
	
	public long getTotal(){
		return this.total;
	}
	
	public void setFail(long fail){
		this.fail = fail;
	}
	
	public long getFail(){
		return this.fail;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Map<String, String> toTag(){
		Map<String, String> tag = new HashMap<String, String>();
		tag.put(MarkDown_AllInOneKey, this.allInOneKey);
		tag.put(MarkDown_MarkDownPolicy, this.policy.toString().toLowerCase());
		tag.put(MarkDown_Status, this.status);
		tag.put(MarkDown_SamplingDuration, this.duration.toString());
		tag.put(MarkDown_MarkDownReason, this.reason.toString().toLowerCase());
		tag.put(CLIENT, this.version);
		return tag;
	}
}
