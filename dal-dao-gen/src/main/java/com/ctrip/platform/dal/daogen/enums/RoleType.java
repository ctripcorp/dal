package com.ctrip.platform.dal.daogen.enums;

public enum RoleType {
	Admin(1), Limited(2);

	private int _value;

	RoleType(int value) {
		_value = value;
	}

	public int getValue() {
		return _value;
	}
}
