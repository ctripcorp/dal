package com.ctrip.sysdev.das.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.msg.RequestObject;
import com.ctrip.sysdev.das.pack.RequestObjectUnPacker;

/**
 * 
 * @author weiw
 * 
 */
public class MessageDecode extends ByteToMessageDecoder {

	private static final Logger logger = LoggerFactory
			.getLogger(MessageDecode.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		RequestObject request = null;

		if (in.readableBytes() < 6) {// available byte < packe head
			return;
		}
		in.markReaderIndex();// mark position=0

		short protocolVersion = in.readShort();

		int dataLength = in.readInt();// packe size

		if (in.readableBytes() < dataLength) {

			in.resetReaderIndex();// go to mark
			return;
		}

		byte[] decoded = new byte[dataLength];
		in.readBytes(decoded);

		request = RequestObjectUnPacker.unpack(decoded);

		if (request != null)
			out.add(request);

	}

}
