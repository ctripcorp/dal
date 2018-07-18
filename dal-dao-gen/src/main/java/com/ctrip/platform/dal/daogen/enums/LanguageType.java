package com.ctrip.platform.dal.daogen.enums;

public enum LanguageType {
	Java(1), CSharp(2);

	private int _value;

	LanguageType(int value) {
		_value = value;
	}

	public int getValue() {
		return _value;
	}
}
