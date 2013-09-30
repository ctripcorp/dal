package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.ctrip.sysdev.das.domain.RequestMessage;
import com.ctrip.sysdev.das.serde.impl.ResponseSerDe;
import com.google.inject.Inject;

public class Netty4ProtocolEncoder extends MessageToByteEncoder<RequestMessage> {

	private ResponseSerDe msgPackSerDe = new ResponseSerDe();

	@Override
	protected void encode(ChannelHandlerContext ctx, RequestMessage msg, ByteBuf out)
			throws Exception {
		System.out.println("..encode..");
		ctx.flush();
	}
}