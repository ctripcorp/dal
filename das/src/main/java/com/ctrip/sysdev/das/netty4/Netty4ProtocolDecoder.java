package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.serde.MsgPackSerDe;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Netty4ProtocolDecoder extends ByteToMessageDecoder {

	private MsgPackSerDe<Request> msgPackSerDe;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	public Netty4ProtocolDecoder(
			@Named("RequestSerDe") MsgPackSerDe msgPackSerDe) {
		this.msgPackSerDe = msgPackSerDe;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		Request request = null; 

		if (in.readableBytes() < 4) {// available byte < packe head
			return;
		}
		in.markReaderIndex();// mark position=0

		int dataLength = in.readInt();// packe size

		if (in.readableBytes() < dataLength) {

			in.resetReaderIndex();// go to mark
			return;
		}
		short protocolVersion = in.readShort();

		byte[] decoded = new byte[dataLength - 2];
		in.readBytes(decoded);
 
		try {
			request = msgPackSerDe.deserialize(decoded);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if (request != null) {
			out.add(request); 
		}
	}
}