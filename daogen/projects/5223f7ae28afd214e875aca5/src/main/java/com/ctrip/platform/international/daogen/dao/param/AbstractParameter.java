package com.ctrip.platform.international.daogen.dao.param;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.msgpack.packer.Packer;
import org.msgpack.type.Value;

import com.ctrip.platform.international.daogen.dao.enums.ParameterType;

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

	@Override
	public PreparedStatement setPreparedStatement(PreparedStatement ps) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParameterIndex(int parameterIndex) {
		
		this.parameterIndex = parameterIndex;
		
	}

	@Override
	public int compareTo(Parameter o) {
		return this.parameterIndex - o.getParameterIndex();
	}

	@Override
	public void pack(Packer packer) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
