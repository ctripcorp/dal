package com.ctrip.platform.dal.daogen.enums;

public enum AddUser {
	Allow(1), Prohibit(2);

	private int _value;

	AddUser(int value) {
		_value = value;
	}

	public int getValue() {
		return _value;
	}
}
