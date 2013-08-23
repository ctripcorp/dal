package com.ctrip.sysdev.das.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.request.DefaultRequest;

/**
 * 
 * @author weiw
 * 
 */
public class RequestDecoder extends ByteToMessageDecoder {

	private static final Logger logger = LoggerFactory
			.getLogger(RequestDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		DefaultRequest request = null;

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

		request = DefaultRequest.unpackFromBytes(decoded);

		if (request != null)
			out.add(request);

	}

}
