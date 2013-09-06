package com.ctrip.platform.dao.param;

import org.msgpack.type.Value;

import com.ctrip.platform.dao.enums.ParameterType;

public class AbstractParameter implements Parameter {
	
	protected int parameterIndex;
	
	protected String parameterLabel;
	
	protected ParameterType parameterType;
	
	protected Value value;

	@Override
	public ParameterType getParameterType() {
		return this.parameterType;
	}

	@Override
	public Value getValue() {
		return this.value;
	}

	@Override
	public int getParameterIndex() {
		return this.parameterIndex;
	}

	@Override
	public String getParameterLabel() {
		return this.parameterLabel;
	}

}
