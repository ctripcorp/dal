package com.ctrip.platform.dal.dao.markdown;

public class Markdown {
	private boolean auto;
	private String name;
	private long markdownTime;
	
	public Markdown(boolean auto, String key){
		this.markdownTime = System.currentTimeMillis();
		this.auto = auto;
		this.name = key;
	}

	public boolean isAuto() {
		return auto;
	}


	public String getName() {
		return name;
	}

	public long getMarkdownTime() {
		return markdownTime;
	}
}
