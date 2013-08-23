package com.ctrip.sysdev.das.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import com.ctrip.sysdev.das.request.DefaultRequest;

public class MessageDecode2 extends LengthFieldBasedFrameDecoder {

	public MessageDecode2(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip);
		// TODO Auto-generated constructor stub
		// int maxFrameLength = 1024;// 可支持的最大长度
		// int lengthFieldOffset = 2;// 长度属性的起始偏移量
		// int lengthFieldLength = 4;// 长度属性占用的字节数
		// int lengthAdjustment = 2; // 总长包含头部信息的时候=负头部信息的字节数
		// int initialBytesToStrip = 6;// 解码后的数据包需要跳过的头部信息的字节数
	}

	@Override
	protected DefaultRequest decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		ByteBuf buffs = (ByteBuf) super.decode(ctx, in);
		DefaultRequest request = DefaultRequest.unpackFromBytes(buffs.array());
		return request;
	}
}
