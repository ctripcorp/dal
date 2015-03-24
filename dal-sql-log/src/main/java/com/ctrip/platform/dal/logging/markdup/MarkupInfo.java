package com.ctrip.platform.dal.logging.markdup;

import java.util.HashMap;
import java.util.Map;

public class MarkupInfo {
	public static final String KEY = "arch.dal.markup.info";
	
	private static final String AllInOneKey = "AllInOneKey";
	private String allinoneKey;
	
	public static final String CLIENT = "Client";
	private String version;
	
	public MarkupInfo(String key, String version){
		this.allinoneKey = key;
		this.version = "Java " + version;
	}
	
	public Map<String, String> toTag(){
		Map<String, String> tag = new HashMap<String, String>();
		tag.put(AllInOneKey, this.allinoneKey);
		tag.put(CLIENT, this.version);
		return tag;
	}
}
