package com.ctrip.sysdev.das.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.ctrip.sysdev.das.msg.MessageObject;
import com.ctrip.sysdev.das.pack.MessageObjectUnPacker;

/**
 * 
 * @author weiw
 * 
 */
public class MessageDecode extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		MessageObject myMessage = null;
		byte[] payload_array = null;

		if (in.readableBytes() < 4) {// available byte < packe head
			return;
		}
		in.markReaderIndex();// mark position=0

		int dataLength = in.readInt();// packe size
		if (in.readableBytes() < dataLength + 4) {
			in.resetReaderIndex();// go to mark
			return;
		}

		byte[] decoded = new byte[dataLength];
		in.readBytes(decoded, 4, dataLength);

		myMessage = new MessageObjectUnPacker().unpack(payload_array);
		if (myMessage != null)
			out.add(myMessage);

	}

}
