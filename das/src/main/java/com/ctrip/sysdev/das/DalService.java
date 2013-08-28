package com.ctrip.sysdev.das;

import io.netty.buffer.ByteBuf;

import com.ctrip.sysdev.das.domain.Request;

public interface DalService {
	public ByteBuf dalService(Request request);
}
