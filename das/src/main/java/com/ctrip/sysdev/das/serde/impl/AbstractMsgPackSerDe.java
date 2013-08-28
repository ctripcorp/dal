package com.ctrip.sysdev.das.serde.impl;

import com.ctrip.sysdev.das.exception.SerDeException;
import com.ctrip.sysdev.das.serde.MsgPackSerDe;

public abstract class AbstractMsgPackSerDe<T> implements MsgPackSerDe<T> {

	public abstract byte[] doSerialize(T obj) throws SerDeException;

	public abstract T doDeserialize(byte[] source) throws SerDeException;

	public abstract void accept(Class<?> c) throws SerDeException;

	@Override
	public byte[] serialize(T object) throws SerDeException {
		accept(object.getClass());
		return doSerialize(object);
	}

	@Override
	public T deserialize(byte[] data) throws SerDeException {
		return doDeserialize(data);
	}
}