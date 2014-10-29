package com.ctrip.platform.dal.logging.markdup;

import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.sql.logging.DalClientVersion;

public class MarkupInfo {
	public static final String KEY = "arch.dal.markup.info";
	
	private static final String AllInOneKey = "AllInOneKey";
	private String allinoneKey;
	
	public static final String CLIENT = "Client";
	private static final String CLIENT_NAME = "Java " + DalClientVersion.version;
	
	public MarkupInfo(String key){
		this.allinoneKey = key;
	}
	
	public Map<String, String> toTag(){
		Map<String, String> tag = new HashMap<String, String>();
		tag.put(AllInOneKey, this.allinoneKey);
		tag.put(CLIENT, CLIENT_NAME);
		return tag;
	}
}
