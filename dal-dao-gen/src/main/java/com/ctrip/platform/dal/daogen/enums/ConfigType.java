package com.ctrip.platform.dal.daogen.enums;

public enum ConfigType {
	Dal(1, "Dal.config"), Datasource(2, "Datasource.xml"), Database(3,
			"Database.config");

	private int _value;

	private String _desc;

	ConfigType(int value, String desc) {
		_value = value;
		_desc = desc;
	}

	public int getValue() {
		return _value;
	}

	public String getDescription() {
		return _desc;
	}
}
