package com.ctrip.platform.dal.daogen.enums;

public enum ConditionType {
	Unknow(-1),
	Equal(0),
	NotEqual(1),
	Great(2),
	Less(3),
	GreatAndEqual(4),
	LessAndEqual(5),
	Between(6),
	Like(7),
	In(8);
	
	private int intVal;
	
	ConditionType(int intVal)
	{
		this.intVal = intVal;
	}
	
	public static ConditionType valueOf(int intVal)
	{
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
		default:
			return Unknow;
		}
	}
	
	public int getIntVal()
	{
		return intVal;
	}
}
