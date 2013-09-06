package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;



import com.ctrip.sysdev.das.domain.RequestMessage;
import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.serde.MsgPackSerDe;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Netty4ProtocolEncoder extends MessageToByteEncoder<RequestMessage> {

	private MsgPackSerDe<Response> msgPackSerDe;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	public Netty4ProtocolEncoder(
			@Named("ResponseSerDe") MsgPackSerDe msgPackSerDe) {
		this.msgPackSerDe = msgPackSerDe;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, RequestMessage msg, ByteBuf out)
			throws Exception {
		System.out.println("..encode..");
		ctx.flush();
	}
}