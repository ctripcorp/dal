package com.ctrip.platform.dal.dao.markdown;

import java.util.HashMap;
import java.util.Map;

public class MarkupInfo {
	public static final String KEY = "arch.dal.markup.info";
	
	private static final String AllInOneKey = "AllInOneKey";
	private String dbKey;
	
	public static final String CLIENT = "Client";
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

	public Map<String, String> toTag(){
		Map<String, String> tag = new HashMap<String, String>();
		tag.put(AllInOneKey, this.dbKey);
		tag.put(CLIENT, this.version);
		return tag;
	}
}
