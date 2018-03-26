package com.ctrip.platform.dal.daogen.domain;

public class W2uiElement  implements Comparable<W2uiElement>{
	
	private String id;
	
	private boolean children;
	
	private String data;
	
	private String text;

	private String icon;
	
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getText() {
		return text;
	}

	public void setText(String name) {
		this.text = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isChildren() {
		return children;
	}

	public void setChildren(boolean isParent) {
		this.children = isParent;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public int compareTo(W2uiElement o) {
		return this.data.compareToIgnoreCase(o.getData());
	}

}
