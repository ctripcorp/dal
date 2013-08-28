package com.ctrip.sysdev.das.serde;

public enum MsgPackSerDeType {
	REQUEST_OBJECT((byte) 1), RESPONSE_OBJECT((byte) 2), CHUNK((byte) 3);

	private MsgPackSerDeType(byte code) {
		this.typeId = code;
	}

	public static MsgPackSerDeType valueOf(byte code) {
		final int i = (code & 0xff) - DEFAULT_TYPE;
		return i < 0 || i >= values().length ? null : values()[i];
	}

	public final byte typeId;

	private static final int DEFAULT_TYPE = values()[0].typeId;

}