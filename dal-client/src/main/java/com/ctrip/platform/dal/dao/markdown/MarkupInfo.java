package com.ctrip.platform.dal.dao.markdown;

public class MarkupInfo {
	private String dbKey;
	private String version;
	private int qualifies;
	
	public MarkupInfo(String key, String version, int qualifies){
		this.dbKey = key;
		this.version = "Java " + version;
		this.qualifies = qualifies;
	}
	
	public String getDbKey() {
		return dbKey;
	}

	public String getVersion() {
		return version;
	}

	public int getQualifies() {
		return qualifies;
	}
}
