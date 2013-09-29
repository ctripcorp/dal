package com.ctrip.platform.dao.param;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.platform.dao.enums.ParameterType;

/**
 * Known issues: 1. You can not batch insert data when binary data exists
 * 
 * @author gawu
 * 
 */
public final class ParameterFactory {

	private ParameterFactory() {
	}

	public static Parameter createNullParameter(int parameterIndex,
			ParameterType sqlType) {
		return new DefaultParameter(parameterIndex, sqlType,
				ValueFactory.createNilValue());
	}

	public static Parameter createBooleanParameter(int parameterIndex,
			boolean value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createByteParameter(int parameterIndex, byte value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createShortParameter(int parameterIndex, short value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createIntParameter(int parameterIndex, int value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createIntArrayParameter(int parameterIndex,
			int[] value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createLongParameter(int parameterIndex, long value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createFloatParameter(int parameterIndex, float value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createDoubleParameter(int parameterIndex,
			double value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createDecimalParameter(int parameterIndex,
			BigDecimal value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createStringParameter(int parameterIndex,
			String value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createStringArrayParameter(int parameterIndex,
			String[] value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createTimestampParameter(int parameterIndex,
			Timestamp value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createByteArrayParameter(int parameterIndex,
			byte[] value) {
		return new DefaultParameter(parameterIndex, value);
	}

	public static Parameter createParameter(int parameterIndex,
			ParameterType parameterType, Value value) {
		return new DefaultParameter(parameterIndex, parameterType, value);
	}

	public static Parameter createParameterFromUnpack(Unpacker unpacker)
			throws IOException {
		return DefaultParameter.unpack(unpacker);
	}

	public static Parameter createParameterList(Parameter... params) {

		Value[] valueArray = new Value[params.length * 3];

		for (int i = 0; i < params.length; i++) {
			int j = i * 3;
			valueArray[j] = ValueFactory.createIntegerValue(params[i]
					.getParameterIndex());
			valueArray[j + 1] = ValueFactory.createIntegerValue(params[i]
					.getParameterType().getIntVal());
			valueArray[j + 2] = params[i].getValue();
		}

		return new DefaultParameter(-1, ParameterType.PARAMARRAY,
				ValueFactory.createArrayValue(valueArray));

	}

}
