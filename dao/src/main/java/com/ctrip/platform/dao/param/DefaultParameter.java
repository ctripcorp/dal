package com.ctrip.platform.dao.param;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.platform.dao.enums.ParameterType;

class DefaultParameter extends AbstractParameter {

	DefaultParameter(int parameterIndex) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.NULL;
		this.value = ValueFactory.createNilValue();
	}

	/**
	 * Initialize AvailableType with a boolean value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, boolean value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.BOOL;
		this.value = ValueFactory.createBooleanValue(value);
	}

	/**
	 * Initialize AvailableType with a byte value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, byte value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.BYTE;
		this.value = ValueFactory.createIntegerValue(value);
	}

	/**
	 * Initialize AvailableType with a short value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, short value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.SHORT;
		this.value = ValueFactory.createIntegerValue(value);
	}

	/**
	 * Initialize AvailableType with an int value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, int value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.INT;
		this.value = ValueFactory.createIntegerValue(value);
	}

	DefaultParameter(int parameterIndex, int[] values) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.INTARRAY;
		Value[] resultValues = new Value[values.length];
		for (int i = 0; i < values.length; i++) {
			resultValues[i] = ValueFactory.createIntegerValue(values[i]);
		}
		this.value = ValueFactory.createArrayValue(resultValues);
	}

	/**
	 * Initialize DefaultParameter with a long value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, long value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.LONG;
		this.value = ValueFactory.createIntegerValue(value);
	}

	/**
	 * Initialize AvailaDefaultParametera float value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, float value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.FLOAT;
		this.value = ValueFactory.createFloatValue(value);
	}

	/**
	 * Initialize AvailableType with a double value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, double value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.DOUBLE;
		this.value = ValueFactory.createFloatValue(value);
	}

	/**
	 * Initialize AvailableType with a BigDecimal value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, BigDecimal value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.DECIMAL;
		this.value = ValueFactory.createFloatValue(value.doubleValue());
	}

	/**
	 * Initialize DefaultParameter with a String value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, String value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.STRING;
		this.value = ValueFactory.createRawValue(value);
	}

	DefaultParameter(int parameterIndex, String[] values) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.STRINGARRAY;
		Value[] resultValues = new Value[values.length];
		for (int i = 0; i < values.length; i++) {
			resultValues[i] = ValueFactory.createRawValue(values[i]);
		}
		this.value = ValueFactory.createArrayValue(resultValues);
	}

	/**
	 * Initialize DefaultParameter with a Timestamp value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, Timestamp value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.DATETIME;
		this.value = ValueFactory.createFloatValue(value.getTime());
	}

	/**
	 * Initialize DefaultParameter with a byte[] value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	DefaultParameter(int parameterIndex, byte[] value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = ParameterType.BYTEARRAY;
		this.value = ValueFactory.createRawValue(value);
	}

	DefaultParameter(int parameterIndex, ParameterType parameterType,
			Value value) {
		this.parameterIndex = parameterIndex;
		this.parameterType = parameterType;
		this.value = value;
	}

	public int getParameterIndex() {
		return this.parameterIndex;
	}

	public ParameterType getParameterType() {
		return this.parameterType;
	}

	public Value getValue() {
		return this.value;
	}

	public static Parameter unpack(Unpacker unpacker) throws IOException {

		unpacker.readArrayBegin();

		int parameterIndex = unpacker.readInt();

		ParameterType parameterType = ParameterType.fromInt(unpacker.readInt());

		Value value = unpacker.readValue();

		unpacker.readArrayEnd();

		return new DefaultParameter(parameterIndex, parameterType, value);
	}

}
