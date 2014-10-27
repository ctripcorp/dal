package com.ctrip.platform.dal.logging.markdup;

import java.util.HashMap;
import java.util.Map;

public class MarkupInfo {
	public static final String KEY = "arch.dal.markup.info";
	
	private static final String AllInOneKey = "AllInOneKey";
	private String allinoneKey;
	
	public MarkupInfo(String key){
		this.allinoneKey = key;
	}
	
	public Map<String, String> toTag(){
		Map<String, String> tag = new HashMap<String, String>();
		tag.put(AllInOneKey, this.allinoneKey);
		tag.put("Language", "Java");
		return tag;
	}
}
