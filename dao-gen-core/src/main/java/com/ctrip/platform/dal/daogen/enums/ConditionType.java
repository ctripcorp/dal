package com.ctrip.platform.dal.daogen.enums;

public enum ConditionType {
	Unknow(-1), Equal(0), NotEqual(1), Great(2), Less(3), GreatAndEqual(4), LessAndEqual(5), Between(6), Like(7), In(
			8), IsNull(9), IsNotNull(10), And(11), Or(12), Not(13), LeftBracket(14), RightBracket(15);

	private int intVal;

	ConditionType(int intVal) {
		this.intVal = intVal;
	}

	public static ConditionType valueOf(int intVal) {
		switch (intVal) {
		case 0:
			return Equal;
		case 1:
			return NotEqual;
		case 2:
			return Great;
		case 3:
			return Less;
		case 4:
			return GreatAndEqual;
		case 5:
			return LessAndEqual;
		case 6:
			return Between;
		case 7:
			return Like;
		case 8:
			return In;
		case 9:
			return IsNull;
		case 10:
			return IsNotNull;
		case 11:
			return And;
		case 12:
			return Or;
		case 13:
			return Not;
		case 14:
			return LeftBracket;
		case 15:
			return RightBracket;
		default:
			return Unknow;
		}
	}

	public int getIntVal() {
		return intVal;
	}
}
