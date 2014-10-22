package com.ctrip.platform.dal.dao.markdown;

public class Markdown {
	private boolean auto;
	private long markdownTime;
	
	public Markdown(boolean auto){
		this.markdownTime = System.currentTimeMillis();
		this.auto = auto;
	}

	public boolean isAuto() {
		return auto;
	}

	public long getMarkdownTime() {
		return markdownTime;
	}
}
