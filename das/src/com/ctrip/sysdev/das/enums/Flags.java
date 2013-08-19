package com.ctrip.sysdev.das.enums;

public enum Flags {
	COMMIT(1),
	TEST(2);

	private int intVal;

	Flags(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
}
