package com.ctrip.platform.dal.dao.markdown;

public class MarkDownInfo {	
	private String dbKey;
	private String version;
	private MarkDownPolicy policy;
	private Long duration;
	private String status;
	private MarkDownReason reason;
	private long total = 0;
	private long fail = 0;

	public MarkDownInfo(String dbKey,String version, MarkDownPolicy policy, long duration){
		this.dbKey = dbKey;
		this.version = "Java " + version;
		this.policy = policy;
		this.duration = duration;
	}
	
	public String getDbKey() {
		return dbKey;
	}

	public String getVersion() {
		return version;
	}

	public MarkDownPolicy getPolicy() {
		return policy;
	}

	public Long getDuration() {
		return duration;
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
}
