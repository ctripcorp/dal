package com.ctrip.platform.dao.param;

import java.io.IOException;

import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.platform.dao.enums.ParameterType;

public interface Parameter {
	
	public int getParameterIndex();
	
	public String getParameterLabel();
	
	public ParameterType getParameterType();
	
	public Value getValue();
	

}
