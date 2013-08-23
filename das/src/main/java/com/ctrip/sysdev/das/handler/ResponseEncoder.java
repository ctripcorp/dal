package com.ctrip.sysdev.das.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.ctrip.sysdev.das.msg.Message;

/**
 * 
 * @author weiw
 * 
 */
public class ResponseEncoder extends MessageToByteEncoder<Message> {

//	private static MessageObjectPacker messageObjectPacker = new MessageObjectPacker();

	@Override
	public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out)
			throws Exception {
//		out.writeBytes(messageObjectPacker.pack(msg));
		ctx.flush();
	}
}