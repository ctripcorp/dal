package com.ctrip.platform.dal.logging.markdup;

import java.util.HashMap;
import java.util.Map;

public class MarkupInfo {
	public static final String KEY = "arch.java.dal.markup.info";
	
	private static final String VOLUMNE = "volume";
	private int volume;
	
	private static final String AllInOneKey = "AllInOneKey";
	private String allinoneKey;
	
	public MarkupInfo(String key, int volume){
		this.allinoneKey = key;
		this.volume = volume;
	}
	
	public Map<String, String> toTag(){
		Map<String, String> tag = new HashMap<String, String>();
		tag.put(AllInOneKey, this.allinoneKey);
		tag.put(VOLUMNE, Integer.toString(this.volume));
		return tag;
	}
}
