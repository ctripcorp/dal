package com.ctrip.sysdev.das.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.ctrip.sysdev.das.msg.MessageObject;

/**
 * 
 * @author weiw
 * 
 */
public class MessageEncoder extends MessageToByteEncoder<MessageObject> {

//	private static MessageObjectPacker messageObjectPacker = new MessageObjectPacker();

	@Override
	public void encode(ChannelHandlerContext ctx, MessageObject msg, ByteBuf out)
			throws Exception {
//		out.writeBytes(messageObjectPacker.pack(msg));
		ctx.flush();
	}
}