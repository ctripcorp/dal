package com.ctrip.platform.dal.logging.markdown;

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
	
	private int total = 0;
	private int fail = 0;

	public MarkDownInfo(String allinoneKey, MarkDownPolicy policy, long duration){
		this.allInOneKey = allinoneKey;
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
	
	public void incrementTotal(){
		this.total ++;
	}
	
	public int getTotal(){
		return this.total;
	}
	
	public void incrementFail(){
		this.fail ++;
	}
	
	public int getFail(){
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
		tag.put("Language", "Java");
		return tag;
	}
}
