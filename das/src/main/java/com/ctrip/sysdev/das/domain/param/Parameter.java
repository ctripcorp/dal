package com.ctrip.sysdev.das.domain.param;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.msgpack.packer.Packer;
import org.msgpack.type.Value;

import com.ctrip.sysdev.das.domain.enums.ParameterType;

public interface Parameter extends Comparable<Parameter> {
	
	public int getParameterIndex();
	
	public void setParameterIndex(int parameterIndex);
	
	public String getParameterLabel();
	
	public ParameterType getParameterType();
	
	public Value getValue();
	
	public PreparedStatement setPreparedStatement(PreparedStatement ps) throws SQLException;
	
	public void pack(Packer packer) throws IOException;

}
